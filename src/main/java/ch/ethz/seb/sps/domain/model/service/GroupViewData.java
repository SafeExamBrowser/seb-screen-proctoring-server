package ch.ethz.seb.sps.domain.model.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.SEB_GROUP;
import ch.ethz.seb.sps.domain.model.Entity;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;
import ch.ethz.seb.sps.domain.model.WithNameDescription;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupViewData implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    public static final String ATTR_EXAM = "exam";
    public static final String FILTER_ATTR_EXAM_NAME = "examName";

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

    @JsonProperty(ATTR_EXAM)
    public final ExamViewData examViewData;

    public GroupViewData(
            final Long id,
            final String uuid,
            final String name,
            final String description,
            final String owner,
            final Long creationTime,
            final Long lastUpdateTime,
            final Long terminationTime,
            final ExamViewData examViewData) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.examViewData = examViewData;
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

    @Override
    public Long getId() {
        return this.id;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getOwnerId() {
        return this.owner;
    }

    @Override
    public Long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public Long getLastUpdateTime() {
        return this.lastUpdateTime;
    }

    @Override
    public Long getTerminationTime() {
        return this.terminationTime;
    }

    public ExamViewData getExamViewData() {
        return this.examViewData;
    }

    public static final Function<Collection<GroupViewData>, List<GroupViewData>> groupSort(final String sort) {

        final String sortBy = PageSortOrder.decode(sort);
        return groups -> {
            final List<GroupViewData> list = groups.stream().collect(Collectors.toList());
            if (StringUtils.isBlank(sort)) {
                return list;
            }

            if (sortBy.equals(Group.FILTER_ATTR_NAME)) {
                list.sort(Comparator.comparing(group -> (group.name != null) ? group.name : StringUtils.EMPTY));
            }
            if (sortBy.equals(FILTER_ATTR_EXAM_NAME)) {
                list.sort(Comparator.comparing(group -> (group.examViewData != null && group.examViewData.name != null)
                        ? group.examViewData.name
                        : StringUtils.EMPTY));
            }
            if (sortBy.equals(Group.FILTER_ATTR_CREATTION_TIME)) {
                list.sort(Comparator.comparing(group -> (group.creationTime != null) ? group.creationTime : 0L));
            }

            if (PageSortOrder.DESCENDING == PageSortOrder.getSortOrder(sort)) {
                Collections.reverse(list);
            }
            return list;
        };
    }
}
