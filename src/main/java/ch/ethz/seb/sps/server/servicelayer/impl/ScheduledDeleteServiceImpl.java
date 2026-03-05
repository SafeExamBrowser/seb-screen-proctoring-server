package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.APIErrorException;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScheduledDeleteDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.ScheduledDeleteService;
import ch.ethz.seb.sps.server.servicelayer.UserService;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Nullable;
import ch.ethz.seb.sps.utils.Result;
import ch.ethz.seb.sps.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

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

    public ScheduledDeleteServiceImpl(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final UserService userService,
            final ScheduledDeleteDAO scheduledDeleteDAO) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.userService = userService;
        this.scheduledDeleteDAO = scheduledDeleteDAO;

    }

    @Override
    public Result<ScheduledDelete> requestScheduledDelete(
            final Long deleteDueTimestampUTC,
            final Long institutionId) {

        return Result.tryCatch(() -> {

            // first check if there is already a pending delete.
            checkPendingExistsAlready();

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

            // first check if there is already a pending delete.
            checkPendingExistsAlready();

            // check consistency
            final Long dueTimeUTC = scheduledDelete.deleteDueTime();
            final Long scheduleTimeUTC = scheduledDelete.scheduleTime();
            final long now = Utils.getMillisecondsNow();

            if (dueTimeUTC == null) {
                throw APIErrorException.ofIllegalArgument(
                        "ScheduledDelete.create",
                        "dueTimeUTC must be provided",
                        Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME);
            }
            if (scheduleTimeUTC == null) {
                throw APIErrorException.ofIllegalArgument(
                        "ScheduledDelete.create",
                        "scheduleTimeUTC must be provided",
                        Domain.SCHEDULED_DELETE.ATTR_SCHEDULE_TIME);
            }
            if (dueTimeUTC >= now) {
                throw APIErrorException.ofIllegalArgument(
                        "ScheduledDelete.create",
                        "dueTimeUTC must be in the past",
                        Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME);
            }
            if (scheduleTimeUTC <= now) {
                throw APIErrorException.ofIllegalArgument(
                        "ScheduledDelete.create",
                        "scheduleTimeUTC must be in the past",
                        Domain.SCHEDULED_DELETE.ATTR_SCHEDULE_TIME);
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
                throw APIErrorException.ofIllegalState(
                        "ScheduledDelete.create",
                        "There is nothing to delete, Deletion info is expected!",
                        "");
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
                        throw APIErrorException.ofIllegalState(
                                "ScheduledDelete.delete",
                                "Running ScheduledDelete Task cannot be deleted since it is in progress",
                                modelId);
                    }

                    return scheduledDeleteDAO
                            .delete(modelId)
                            .map(keys -> new EntityKey(modelId, EntityType.SCHEDULED_DELETE))
                            .getOrThrow();
                });
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
                    deleteInfo.put(Domain.EXAM.ATTR_START_TIME, String.valueOf(exam.startTime));

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

    private void putGroupData(final Group group, final Map<String, String> data) {
        data.put("group_" + group.id + "_name", group.name);
        data.put("group_" + group.id + "_uuid", group.uuid);

        sessionDAO
                .allSessionCount(group.id)
                .onSuccess( count -> data.put("group_" + group.id + "_sessionCount", String.valueOf(count)));
    }

    private void checkPendingExistsAlready() {
        Nullable<ScheduledDelete> pending = scheduledDeleteDAO
                .getDeleteReadyForProcessing()
                .getOrThrow();

        if (!pending.isNull()) {
            throw APIErrorException.ofIllegalState(
                    "ScheduledDelete",
                    "There is already a pending ScheduledDelete. Only one is allowed",
                    pending.element.getModelId());
        }
    }
}
