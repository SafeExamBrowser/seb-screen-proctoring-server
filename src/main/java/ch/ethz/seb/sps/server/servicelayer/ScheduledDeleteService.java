package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.utils.Result;
import org.joda.time.DateTimeZone;
import org.springframework.context.event.EventListener;

import java.util.Collection;
import java.util.Set;


public interface ScheduledDeleteService {

    @EventListener(ServiceInitEvent.class)
    void init();

    /** Create a new scheduled delete task and marks all exams that are older than the given time
     * and are not marked as exclude from deletion (deletion_time < 0)
     * The task will be scheduled for midnight.
     *
     * @param deleteDueTimestamp the due time stamp (UTC) for that all older exams get deleted (mandatory)
     * @return the prepared schedule with report of all exams that are going to be deleted.*/
    Result<ScheduledDelete> requestScheduledDelete(Long deleteDueTimestamp);

    Result<ScheduledDelete> createScheduledDelete(ScheduledDelete scheduledDelete);

    Result<Collection<EntityKey>> markExamsReadyForDeletion(Collection<String> examUUIDs);

    Result<Collection<EntityKey>> excludeExamsFromDeletion(Collection<String> examUUIDs);

}
