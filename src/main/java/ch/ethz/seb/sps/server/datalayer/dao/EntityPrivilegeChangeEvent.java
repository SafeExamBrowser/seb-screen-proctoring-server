package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.utils.Pair;
import org.springframework.context.ApplicationEvent;

public class EntityPrivilegeChangeEvent extends ApplicationEvent {

    public final EntityType entityType;
    public final Long entityId;

    public EntityPrivilegeChangeEvent(final EntityType entityType, final Long entityId) {
        super(new Pair<>(entityType, entityId));
        this.entityType = entityType;
        this.entityId = entityId;
    }
}
