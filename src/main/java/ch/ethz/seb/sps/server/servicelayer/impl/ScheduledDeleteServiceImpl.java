package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScheduledDeleteDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.ScheduledDeleteService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Lazy
@Component
public class ScheduledDeleteServiceImpl implements ScheduledDeleteService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledDeleteServiceImpl.class);
    public static final Logger INIT_LOGGER = LoggerFactory.getLogger("SERVICE_INIT");

    private final ExamDAO examDAO;
    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;
    private final UserService userService;
    private final ScheduledDeleteDAO scheduledDeleteDAO;
    private final ServiceInfo serviceInfo;
    private final TaskScheduler taskScheduler;

    public ScheduledDeleteServiceImpl(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final UserService userService,
            final ScheduledDeleteDAO scheduledDeleteDAO,
            final ServiceInfo serviceInfo,
            @Qualifier(value = ServiceConfig.SYSTEM_SCHEDULER)  final TaskScheduler taskScheduler) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.userService = userService;
        this.scheduledDeleteDAO = scheduledDeleteDAO;
        this.serviceInfo = serviceInfo;
        this.taskScheduler = taskScheduler;

    }

    @Override
    public void init() {

        INIT_LOGGER.info("---->");
        INIT_LOGGER.info("----> Initialize Scheduled delete service");
        INIT_LOGGER.info("---->   Update every hour to check and process pending deletions");
        INIT_LOGGER.info("---->");

        // triggered every hour...
        this.taskScheduler.scheduleAtFixedRate(
                this::update,
                Instant.now().plusMillis(Constants.HOUR_IN_MILLIS),
                Duration.ofMillis(Constants.HOUR_IN_MILLIS));
    }

    @Override
    public Result<ScheduledDelete> createScheduledDelete(
            final Long deleteDueTimestamp,
            final Long scheduledTimestamp,
            final DateTimeZone referenceTimezone) {

        return Result.tryCatch(() -> {

            // prepare times
            // use either given referenceTimezone or UTC
            final DateTimeZone refTimeZone = referenceTimezone != null ? referenceTimezone : DateTimeZone.UTC;
            // use either given scheduledTimestamp or schedule to midnight reverencing to refTimeZone
            Long scheduleTime = scheduledTimestamp;
            if (scheduleTime == null) {
                final DateTime scheduleDateTime = DateTime
                        .now(refTimeZone)                   // get now in reference time zone
                        .plusDays(1)                      // add one day and...
                        .withTimeAtStartOfDay()             // go back to start of day still in reference time zone
                        .toDateTime(DateTimeZone.UTC);      // now convert to UTC (only UTC goes to store)
                scheduleTime = scheduleDateTime.getMillis();
            }

            // deleteDueTimestamp is expected already in UTC
            final DateTime deleteDueDateTime = new DateTime(deleteDueTimestamp, refTimeZone);

            log.info("Schedule delete for dueTime: {} -- UTC: {} at: {} -- UTC: {}",
                    Utils.formatDate(deleteDueDateTime.toDateTime(refTimeZone)),
                    Utils.formatDate(deleteDueDateTime),
                    Utils.formatDate(new DateTime(scheduleTime,refTimeZone)),
                    Utils.formatDate(new DateTime(scheduleTime,DateTimeZone.UTC)));

            // get involved Exams and Groups
            Collection<Exam> exams = examDAO
                    .getExamsForScheduledDeletion(deleteDueTimestamp)
                    .getOrThrow();

            final List<ScheduledDeleteInfo> deleteInfos = new ArrayList<>();
            final List<Group> noExamGroups = new ArrayList<>();
            final Map<Long, Set<Group>> groupsWithExam = new HashMap<>();
            groupDAO
                    .getGroupsForScheduledDeletion(deleteDueTimestamp)
                    .getOrThrow()
                    .forEach(group -> {
                            if (group.exam_id != null) {
                                groupsWithExam.computeIfAbsent(group.exam_id , key -> new HashSet<>()).add(group);
                            } else {
                                noExamGroups.add(group);
                            }
                    });

            // create info
            exams.forEach(exam -> {
                Map<String, String> deleteInfo = new HashMap<>();
                deleteInfo.put("examName", exam.name);
                deleteInfo.put("examType", exam.type);

                Set<Group> groups = groupsWithExam.get(exam.id);
                if (groups != null) {
                    groups.forEach( group -> putGroupData(group, deleteInfo));
                }
                deleteInfos.add(new ScheduledDeleteInfo(
                    null,
                        null,
                        ScheduledDeleteInfo.State.PENDING,
                        exam.uuid,
                        deleteInfo,
                        null
                ));
            });
            noExamGroups.forEach( group -> {
                Map<String, String> deleteInfo = new HashMap<>();
                putGroupData(group, deleteInfo);
                deleteInfos.add(new ScheduledDeleteInfo(
                        null,
                        null,
                        ScheduledDeleteInfo.State.PENDING,
                        null,
                        deleteInfo,
                        null
                ));
            });

            final ServerUser currentUser = userService.getCurrentUser();
            if (deleteInfos.isEmpty()) {
                // we have nothing to delete here for the given schedule. Return an empty model (no PK)
                return new ScheduledDelete(
                        null,
                        ScheduledDelete.State.FINISHED,
                        deleteDueTimestamp,
                        scheduleTime,
                        Utils.getMillisecondsNow(),
                        Utils.getMillisecondsNow(),
                        currentUser.uuid(),
                        Collections.emptyList()
                );
            }

            // create scheduled delete
            return scheduledDeleteDAO.createNew(new ScheduledDelete(
                    null,
                    ScheduledDelete.State.PENDING,
                    deleteDueTimestamp,
                    scheduleTime,
                    null,
                    null,
                    currentUser.uuid(),
                    deleteInfos)).getOrThrow();
        });
    }

    @Override
    public Result<Collection<EntityKey>> markExamsReadyForDeletion(final Collection<String> examUUIDs) {
        return examDAO.markExamsReadyForDeletion(examUUIDs);
    }

    @Override
    public Result<Collection<EntityKey>> excludeExamsFromDeletion(final Collection<String> examUUIDs) {
        return examDAO.excludeExamsFromDeletion(examUUIDs);
    }

    private void update() {

        // only master service should do scheduled deletion
        if (!serviceInfo.isMaster()) {
            return;
        }

        log.info("Check for pending scheduled deletions to process...");

        // get all pending schedules that are ready for processing and process one after another
        scheduledDeleteDAO
                .getSchedulesReadyForProcessing()
                .onError(error -> log.error("Failed to fetch pending scheduled deletes: {}", error.getMessage()))
                .onSuccess(deletes -> {
                    log.info("Found {} scheduled deletions for processing", deletes.size());
                    deletes.forEach(this::processDelete);
                });
    }

    private void processDelete(final ScheduledDelete delete) {
        try {

            log.info("**************************************");
            log.info("**** Start ScheduledDelete: {}", delete);
            scheduledDeleteDAO.startProcessing(delete.id());

            delete.info().forEach(single -> {
                if (single.examUUID() == null) {
                    deleteGroup(single);
                } else {
                    deleteExam(single);
                }
            });

            scheduledDeleteDAO.endProcessing(delete.id());

            log.info("**** End ScheduledDelete");
            log.info("**************************************");

        } catch (Exception e) {
            log.error("Failed to process ScheduledDelete: {}", e.getMessage());
        }
    }

    private void deleteExam(final ScheduledDeleteInfo info) {
        try {

            log.info("Start deleting exam: {}", info);


            scheduledDeleteDAO.startSingleDeletion(info.id());

            // check again that Exam is not marked as exclude from deletion
            final Exam exam = examDAO.byModelId(info.examUUID()).getOrThrow();
            if (exam.deletionTime == null || exam.deletionTime < 0) {
                // exam is now excluded from deletion so skip it and mark in error info
                scheduledDeleteDAO.endSingleDeletion(info.id(), "Exam is excluded from deletion");
                return;
            }

            long start = Utils.getMillisecondsNow();
            final Result<Collection<EntityKey>> delete = examDAO.delete(info.examUUID());
            final String error = delete.getError() != null ? delete.getError().getMessage() : null;
            scheduledDeleteDAO.endSingleDeletion(info.id(), error);

            log.info("Finished deleting exam. Took {} milliseconds. With error: {}",
                    (Utils.getMillisecondsNow() - start),
                    error != null ? error : "none");

        } catch (Exception e) {
            log.error("Failed to process exam delete for: {} cause: {}", info, e.getMessage());
        }
    }

    private void deleteGroup(final ScheduledDeleteInfo info) {
        try {
            String groupUUID = info.deletionInfo()
                    .entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().endsWith("_uuid"))
                    .findFirst()
                    .map(Map.Entry::getValue)
                    .orElse(null);

            log.info("Start deleting dangling group: {}", info);
            long now = Utils.getMillisecondsNow();

            scheduledDeleteDAO.startSingleDeletion(info.id());
            final Result<Collection<EntityKey>> delete = groupDAO.delete(groupUUID);
            final String error = delete.getError() != null ? delete.getError().getMessage() : null;
            scheduledDeleteDAO.endSingleDeletion(info.id(), error);

            log.info("Finished deleting dangling group. Took {} milliseconds. With error: {}",
                    (Utils.getMillisecondsNow() - now),
                    error != null ? error : "none");
        } catch (Exception e) {
            log.error("Failed to process dangling group delete for: {} cause: {}", info, e.getMessage());
        }
    }

    private void putGroupData(final Group group, final Map<String, String> data) {
        data.put("group_" + group.id + "_name", group.name);
        data.put("group_" + group.id + "_uuid", group.uuid);

        sessionDAO
                .allSessionCount(group.id)
                .onSuccess( count -> data.put("group_" + group.id + "_sessionCount", String.valueOf(count)));
    }
}
