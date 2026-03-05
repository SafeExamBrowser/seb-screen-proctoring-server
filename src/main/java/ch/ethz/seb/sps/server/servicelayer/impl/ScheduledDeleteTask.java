package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.datalayer.dao.*;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

@Lazy
@Component
public class ScheduledDeleteTask {

    private static final Logger log = LoggerFactory.getLogger(ScheduledDeleteTask.class);
    public static final Logger INIT_LOGGER = LoggerFactory.getLogger("SERVICE_INIT");

    private final ExamDAO examDAO;
    private final GroupDAO groupDAO;
    private final SessionDAO sessionDAO;

    private final ScheduledDeleteDAO scheduledDeleteDAO;
    private final ServiceInfo serviceInfo;
    private final TaskScheduler taskScheduler;

    public ScheduledDeleteTask(
            final ExamDAO examDAO,
            final GroupDAO groupDAO,
            final SessionDAO sessionDAO,
            final ScheduledDeleteDAO scheduledDeleteDAO,
            final ServiceInfo serviceInfo,
            @Qualifier(value = ServiceConfig.SYSTEM_SCHEDULER) final TaskScheduler taskScheduler) {

        this.examDAO = examDAO;
        this.groupDAO = groupDAO;
        this.sessionDAO = sessionDAO;
        this.scheduledDeleteDAO = scheduledDeleteDAO;
        this.serviceInfo = serviceInfo;
        this.taskScheduler = taskScheduler;
    }

    @EventListener(ServiceInitEvent.class)
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

    private void update() {

        // only master service should do scheduled deletion
        if (!serviceInfo.isMaster()) {
            return;
        }

        log.info("Check for pending scheduled deletions to process...");

        // get all pending schedules that are ready for processing and process one after another
        scheduledDeleteDAO
                .getDeleteReadyForProcessing()
                .onError(error -> log.error("Failed to fetch pending scheduled deletes: {}", error.getMessage()))
                .onSuccess(delete -> {
                    try {

                        if (delete.isNull()) {
                            return;
                        }

                        final ScheduledDelete element = delete.getElement();
                        final long scheduleTime = element.scheduleTime();
                        final long now = Utils.getMillisecondsNow();

                        if (log.isDebugEnabled()) {
                            log.debug("Found pending ScheduledDelete with schedule time: {} now is: {}", scheduleTime, now);
                        }

                        if (scheduleTime <= now) {
                            log.info("Found scheduled deletion for processing: {}", element);
                            this.processDelete(element);
                        } else {
                            if (log.isDebugEnabled()) {
                                log.debug("Skip scheduled delete since schedule time is still older");
                            }
                        }

                    } catch (Exception e) {
                        log.error("Failed to verify pending ScheduledDelete: {}", e.getMessage());
                    }
                });
    }

    private void processDelete(final ScheduledDelete delete) {
        try {

            log.info("**************************************");
            log.info("**** Start ScheduledDelete: {}", delete);

            // mark as RUNNING
            scheduledDeleteDAO.startProcessing(delete.getPK());

            // delete one by one
            delete.info().forEach(this::deleteExam);

            // mark as FINISHED
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
            long start = Utils.getMillisecondsNow();

            // mark as RUNNING
            scheduledDeleteDAO.startSingleDeletion(info.id());

            try {

                // first delete groups one be one
                final Collection<Group> groups = groupDAO.byExamUUID(info.examUUID()).getOrThrow();
                groups.forEach(this::deleteGroup);

                // then delete the exam
                // TODO this is for testing, remove it and reapply real delete
                log.info("********** DEBUG ScheduledDelete --> delete Exam: {}", info.examUUID());
                //examDAO.delete(info.examUUID()).getOrThrow();

                // mark as FINISHED no error
                scheduledDeleteDAO.endSingleDeletion(info.id(), null);

            } catch (Exception ee) {
                log.error("Failed to delete Exam: {}, cause: {}", info, ee.getMessage());
                // mark as FINISHED with error
                scheduledDeleteDAO.endSingleDeletion(info.id(), ee.getMessage());
            }

            log.info("Finished deleting exam. Took {} milliseconds", (Utils.getMillisecondsNow() - start));

        } catch (Exception e) {
            log.error("Failed to process exam delete for: {} cause: {}", info, e.getMessage());
        }
    }

    private void deleteGroup(final Group group) {

        // first delete all sessions of the group
        Collection<String> sessionsIdsOfGroup = sessionDAO
                .allLiveSessionUUIDsByGroupId(group.id)
                .getOrThrow();

        // delete sessions one by one
        // TODO this is for testing, remove it and reapply real delete
        log.info("********** DEBUG ScheduledDelete --> delete Sessions: {} of group: {}", sessionsIdsOfGroup, group);
        //sessionsIdsOfGroup.forEach(sessionDAO::delete);

        // finally delete the group
        // TODO this is for testing, remove it and reapply real delete
        log.info("********** DEBUG ScheduledDelete --> delete Group: {}", group);
        //groupDAO.delete(group.getModelId()).getOrThrow();
    }
}
