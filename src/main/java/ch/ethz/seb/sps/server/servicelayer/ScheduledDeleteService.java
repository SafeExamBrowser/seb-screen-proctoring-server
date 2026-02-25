package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.utils.Result;
import org.joda.time.DateTimeZone;
import org.springframework.context.event.EventListener;

import java.util.Collection;


public interface ScheduledDeleteService {

    @EventListener(ServiceInitEvent.class)
    void init();

    /** Create a new scheduled delete task and marks all exams that are older than the given time
     * and are not marked as exclude from deletion (deletion_time < 0)
     * The task will be scheduled for midnight.
     *
     * @param deleteDueTimestamp the due time stamp (UTC) for that all older exams get deleted
     * @param referenceTimezone a reference time zone to schedule the delete to midnight regarding this given time zone. UTC if null
     * @return the prepared schedule with report of all exams that are going to be deleted.*/
    Result<ScheduledDelete> createScheduledDelete(
            Long deleteDueTimestamp,
            DateTimeZone referenceTimezone);

    Result<Collection<EntityKey>> markExamsReadyForDeletion(Collection<String> examUUIDs);

    Result<Collection<EntityKey>> excludeExamsFromDeletion(Collection<String> examUUIDs);

}
