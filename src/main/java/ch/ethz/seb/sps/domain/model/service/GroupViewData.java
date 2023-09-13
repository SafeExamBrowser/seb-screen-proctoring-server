package ch.ethz.seb.sps.domain.model.service;

import ch.ethz.seb.sps.domain.Domain.EXAM;
import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;
import ch.ethz.seb.sps.domain.model.WithNameDescription;
import ch.ethz.seb.sps.utils.Utils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupViewData implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    @JsonProperty(SEB_GROUP.ATTR_ID)
    public final Long id;

    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String uuid;

    @JsonProperty(SEB_GROUP.ATTR_NAME)
    public final String name;

    @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION)
    public final String description;

    @JsonProperty(SEB_GROUP.ATTR_OWNER)
    public final String owner;

    @JsonProperty(SEB_GROUP.ATTR_CREATION_TIME)
    public final Long creationTime;

    @JsonProperty(SEB_GROUP.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @JsonProperty(SEB_GROUP.ATTR_TERMINATION_TIME)
    public final Long terminationTime;
    @JsonProperty(EXAM.ATTR_EXAM)
    public final ExamViewData examViewData;

    @JsonIgnore
    public final Collection<EntityPrivilege> entityPrivileges;

    public GroupViewData(
            final Long id,
            final String uuid,
            final String name,
            final String description,
            final String owner,
            final Long creationTime,
            final Long lastUpdateTime, Long terminationTime,
            final ExamViewData examViewData,
            final Collection<EntityPrivilege> entityPrivileges
    ) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.examViewData = examViewData;
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
    }


    @Override
    public EntityType entityType() {
        return EntityType.SEB_GROUP;
    }

    @Override
    public Collection<EntityPrivilege> getEntityPrivileges() {
        return null;
    }

    @Override
    public String getModelId() {
        return (this.uuid != null)
                ? this.uuid
                : (this.id != null)
                ? String.valueOf(this.id)
                : null;
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getOwner() {
        return owner;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public Long getTerminationTime() {
        return terminationTime;
    }

    public ExamViewData getExamViewData() {
        return examViewData;
    }



    public static final Function<Collection<GroupViewData>, List<GroupViewData>> groupSort(final String sort) {

        final String sortBy = PageSortOrder.decode(sort);
        return groups -> {
            final List<GroupViewData> list = groups.stream().collect(Collectors.toList());
            return list;
        };
    }
}
