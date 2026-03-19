package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.SessionDeletionInfo;
import ch.ethz.seb.sps.utils.Result;

import java.util.Collection;


public interface ScheduledDeleteService {

    /** Create a new scheduled delete task and marks all exams that are older than the given time
     * and are not marked as exclude from deletion (deletion_time < 0)
     * The task will be scheduled for midnight.
     *
     * @param deleteDueTimestampUTC the due time stamp (UTC) for that all older exams get deleted (mandatory)
     * @return the prepared schedule with report of all exams that are going to be deleted.*/
    Result<ScheduledDelete> requestScheduledDelete(Long deleteDueTimestampUTC, Long institutionId);

    Result<ScheduledDelete> createScheduledDelete(ScheduledDelete scheduledDelete);

    Result<EntityKey> deleteScheduledDelete(String modelId);

    Result<Collection<SessionDeletionInfo>> getSessionDeletionReport(String searchName, Long deleteDueTimestampUTC);

    Result<Collection<SessionDeletionInfo>> deleteSessions(String searchName, Long deleteDueTimeUTC);
}

