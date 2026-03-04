package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
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
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
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
    public Result<ScheduledDelete> requestScheduledDelete(
            final Long deleteDueTimestampUTC,
            final Long institutionId) {

        return Result.tryCatch(() -> {
            final ServerUser currentUser = userService.getCurrentUser();

            // get involved Exams and Groups
            final Collection<Exam> exams = examDAO
                    .getExamsForScheduledDeletion(deleteDueTimestampUTC)
                    .getOrThrow();

            // create info from exams
            final List<ScheduledDeleteInfo> deleteInfos = getScheduledDeleteInfos(institutionId, exams);

            // create requested ScheduledDelete
            return new ScheduledDelete(
                    null,
                    ScheduledDelete.State.PENDING,
                    deleteDueTimestampUTC,
                    null,
                    null,
                    null,
                    currentUser.uuid(),
                    deleteInfos
            );
        });
    }

    @Override
    public Result<ScheduledDelete> createScheduledDelete(final ScheduledDelete scheduledDelete) {

        return Result.tryCatch(() -> {
            // check consistency
            final Long dueTimeUTC = scheduledDelete.deleteDueTime();
            final Long scheduleTimeUTC = scheduledDelete.scheduleTime();
            final long now = Utils.getMillisecondsNow();

            if (dueTimeUTC == null) {
                throw new IllegalArgumentException("dueTimeUTC must be provided");
            }
            if (scheduleTimeUTC == null) {
                throw new IllegalArgumentException("dueTimeUTC must be provided");
            }
            if (dueTimeUTC >= now) {
                throw new IllegalArgumentException("dueTimeUTC must be in the past");
            }
            if (scheduleTimeUTC <= now) {
                throw new IllegalArgumentException("dueTimeUTC must be in the past");
            }

            String ownerUUID = scheduledDelete.ownerUUID();
            if (StringUtils.isBlank(ownerUUID) || !userService.existsByUUID(ownerUUID)) {
                ownerUUID = userService.getCurrentUserUUIDOrNull();
                if (ownerUUID == null) {
                    ownerUUID = "[NOT_FOUND]";
                }
            }

            final Collection<ScheduledDeleteInfo> info = scheduledDelete.info();
            if (scheduledDelete.info() == null || info.isEmpty()) {
                throw new IllegalArgumentException("There is nothing to delete, Deletion info is expected!");
            }

            // create valid and full ScheduledDeleteInfo for each exam
            List<Exam> exams = info
                    .stream()
                    .map(i -> examDAO
                            .byModelId(i.examUUID())
                            .onError(error -> log.error("Failed to get Exam for given UUID: {}", i))
                            .getOr(null))
                    .filter(Objects::nonNull)
                    .toList();

            final List<ScheduledDeleteInfo> scheduledDeleteInfos = getScheduledDeleteInfos(null, exams);
            final ScheduledDelete fullScheduledDelete = new ScheduledDelete(
                    null,
                    ScheduledDelete.State.PENDING,
                    dueTimeUTC,
                    scheduleTimeUTC,
                    null, null,
                    ownerUUID,
                    scheduledDeleteInfos
            );

            final ScheduledDelete result = scheduledDeleteDAO
                    .createNew(fullScheduledDelete)
                    .getOrThrow();

            log.info("Created full scheduled delete: {}", result);

            return result;
        });
    }

    @Override
    public Result<EntityKey> deleteScheduledDelete(final String modelId) {
        return scheduledDeleteDAO.byModelId(modelId)
                .map(scheduledDelete -> {
                    if (scheduledDelete.state() == ScheduledDelete.State.RUNNING) {
                        throw new IllegalArgumentException("Running ScheduledDelete Task cannot be deleted since it is in progress");
                    }

                    return scheduledDeleteDAO
                            .delete(modelId)
                            .map(keys -> new EntityKey(modelId, EntityType.SCHEDULED_DELETE))
                            .getOrThrow();
                });
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
            scheduledDeleteDAO.startProcessing(delete.getPK());

            delete.info().forEach(single -> {
                if (single.examUUID() == null) {
                    deleteGroup(single);
                } else {
                    deleteExam(single);
                }
            });

            scheduledDeleteDAO.endProcessing(delete.getPK());

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

    @NotNull
    private List<ScheduledDeleteInfo> getScheduledDeleteInfos(
            final Long institutionId,
            final Collection<Exam> exams) {

        return exams
                .stream()
                .map(exam -> {

                    // check institutionId if available and skip if not equals
                    if (institutionId != null && exam.institutionId != null && !Objects.equals(institutionId, exam.institutionId)) {
                        return null;
                    }

                    Map<String, String> deleteInfo = new HashMap<>();
                    deleteInfo.put(Domain.EXAM.ATTR_NAME, exam.name);
                    deleteInfo.put(Domain.EXAM.ATTR_TYPE, exam.type);
                    if (exam.institutionId != null) {
                        deleteInfo.put(Domain.EXAM.ATTR_INSTITUTION_ID, String.valueOf(exam.institutionId));
                    }
                    if (!exam.supporter.isEmpty()) {
                        deleteInfo.put(Domain.EXAM.ATTR_SUPPORTER, StringUtils.join(exam.supporter, Constants.LIST_SEPARATOR));
                    }

                    Collection<Group> groups = groupDAO.byExamId(exam.id).getOr(Collections.emptyList());
                    if (groups != null) {
                        groups.forEach(group -> putGroupData(group, deleteInfo));
                    }

                    return new ScheduledDeleteInfo(
                            null,
                            null,
                            ScheduledDeleteInfo.State.PENDING,
                            exam.uuid,
                            deleteInfo,
                            null
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }
}
