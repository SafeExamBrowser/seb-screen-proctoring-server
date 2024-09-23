/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
import ch.ethz.seb.sps.utils.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_ID)
    public final Long id;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_UUID)
    public final String uuid;

    @JsonProperty(SEB_GROUP.ATTR_NAME)
    @NotNull(message = "clientaccess:name:notNull")
    @Size(min = 3, max = 255, message = "group:name:size:{min}:{max}:${validatedValue}")
    public final String name;

    @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION)
    @Size(max = 4000, message = "group:description:size:{max}:${validatedValue}")
    public final String description;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_OWNER)
    public final String owner;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_CREATION_TIME)
    public final Long creationTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(SEB_GROUP.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonProperty(SEB_GROUP.ATTR_EXAM_ID)
    public final Long exam_id;

    @JsonIgnore
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public Group(
            @JsonProperty(SEB_GROUP.ATTR_ID) final Long id,
            @JsonProperty(SEB_GROUP.ATTR_UUID) final String uuid,
            @JsonProperty(SEB_GROUP.ATTR_NAME) final String name,
            @JsonProperty(SEB_GROUP.ATTR_DESCRIPTION) final String description,
            @JsonProperty(SEB_GROUP.ATTR_OWNER) final String owner,
            @JsonProperty(SEB_GROUP.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(SEB_GROUP.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(SEB_GROUP.ATTR_TERMINATION_TIME) final Long terminationTime,
            @JsonProperty(SEB_GROUP.ATTR_EXAM_ID) final Long exam_id) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.exam_id = exam_id;
        this.entityPrivileges = null;
    }

    public Group(
            final Long id,
            final String uuid,
            final String name,
            final String description,
            final String owner,
            final Long creationTime,
            final Long lastUpdateTime,
            final Long terminationTime,
            final Long exam_id,
            final Collection<EntityPrivilege> entityPrivileges) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.exam_id = exam_id;
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
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
    public EntityType entityType() {
        return EntityType.SEB_GROUP;
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

    public Long getExam_id() {
        return this.exam_id;
    }

    @Override
    public String getOwnerId() {
        return this.owner;
    }

    @Override
    public Collection<EntityPrivilege> getEntityPrivileges() {
        return this.entityPrivileges;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.uuid);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Group other = (Group) obj;
        return Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("Group [id=");
        builder.append(this.id);
        builder.append(", uuid=");
        builder.append(this.uuid);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", description=");
        builder.append(this.description);
        builder.append(", owner=");
        builder.append(this.owner);
        builder.append(", creationTime=");
        builder.append(this.creationTime);
        builder.append(", lastUpdateTime=");
        builder.append(this.lastUpdateTime);
        builder.append(", terminationTime=");
        builder.append(this.terminationTime);
        builder.append(", entityPrivileges=");
        builder.append(this.exam_id);
        builder.append(", exam_id=");
        builder.append(this.entityPrivileges);
        builder.append("]");
        return builder.toString();
    }

    public static final Function<Collection<Group>, List<Group>> groupSort(final String sort) {

        final String sortBy = PageSortOrder.decode(sort);
        return groups -> {
            final List<Group> list = groups.stream().collect(Collectors.toList());
            if (StringUtils.isBlank(sort)) {
                return list;
            }

            if (sortBy.equals(Group.FILTER_ATTR_NAME)) {
                list.sort(Comparator.comparing(group -> (group.name != null) ? group.name : StringUtils.EMPTY));
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
