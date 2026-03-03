package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScheduledDelete(@JsonProperty(Domain.SCHEDULED_DELETE.ATTR_ID) Long id,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_STATE) State state,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME) Long deleteDueTime,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_SCHEDULE_TIME) Long scheduleTime,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_START_TIME) Long startTime,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_END_TIME) Long endTime,
                              @JsonProperty(Domain.SCHEDULED_DELETE.ATTR_OWNER) String ownerUUID,
                              @JsonProperty(ATTR_INFO) Collection<ScheduledDeleteInfo> info) implements Entity {

    public static final String ATTR_INFO = "info";

    public enum State {
        PENDING,
        RUNNING,
        FINISHED
    }

    @JsonCreator
    public ScheduledDelete {}


    @Override
    public String getModelId() {
        return String.valueOf(id);
    }

    @Override
    public Long getPK() {
        return id;
    }

    @Override
    public String getName() {
        return Utils.formatDate(Utils.toDateTimeUTC(deleteDueTime));
    }

    @Override
    public EntityType entityType() {
        return EntityType.SCHEDULED_DELETE;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ScheduledDelete that = (ScheduledDelete) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ScheduledDelete{" +
                "id=" + id +
                ", state=" + state +
                ", deleteDueTime=" + deleteDueTime +
                ", scheduleTime=" + scheduleTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", ownerUUID='" + ownerUUID + '\'' +
                ", info=" + info +
                '}';
    }
}
