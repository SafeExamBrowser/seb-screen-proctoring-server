/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.service;

import java.util.*;
import java.util.function.Function;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.EXAM;
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

@JsonIgnoreProperties(ignoreUnknown = true)
public class Exam implements Entity, OwnedEntity, WithNameDescription, WithEntityPrivileges, WithLifeCycle {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_ID)
    public final Long id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_UUID)
    public final String uuid;

    @JsonProperty(EXAM.ATTR_NAME)
    @NotNull(message = "exam:name:notNull")
    @Size(min = 3, max = 255, message = "exam:name:size:{min}:{max}:${validatedValue}")
    public final String name;

    @JsonProperty(EXAM.ATTR_DESCRIPTION)
    @Size(max = 4000, message = "exam:description:size:{max}:${validatedValue}")
    public final String description;

    @JsonProperty(EXAM.ATTR_URL)
    @Size(max = 255, message = "exam:url:size:{max}:${validatedValue}")
    public final String url;

    @JsonProperty(EXAM.ATTR_TYPE)
    @Size(max = 45, message = "exam:type:size:{max}:${validatedValue}")
    public final String type;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_OWNER)
    public final String owner;
    
    @JsonProperty(EXAM.ATTR_SUPPORTER)
    public final Collection<String> supporter;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_CREATION_TIME)
    public final Long creationTime;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(EXAM.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonProperty(EXAM.ATTR_START_TIME)
    public final Long startTime;

    @JsonProperty(EXAM.ATTR_END_TIME)
    public final Long endTime;

    @JsonProperty(EXAM.ATTR_DELETION_TIME)
    public final Long deletionTime;
    
    @JsonIgnore
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public Exam(
            @JsonProperty(EXAM.ATTR_ID) final Long id,
            @JsonProperty(EXAM.ATTR_UUID) final String uuid,
            @JsonProperty(EXAM.ATTR_NAME) final String name,
            @JsonProperty(EXAM.ATTR_DESCRIPTION) final String description,
            @JsonProperty(EXAM.ATTR_URL) final String url,
            @JsonProperty(EXAM.ATTR_TYPE) final String type,
            @JsonProperty(EXAM.ATTR_OWNER) final String owner,
            @JsonProperty(EXAM.ATTR_SUPPORTER)  Collection<String> supporter,
            @JsonProperty(EXAM.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(EXAM.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(EXAM.ATTR_TERMINATION_TIME) final Long terminationTime,
            @JsonProperty(EXAM.ATTR_START_TIME) final Long startTime,
            @JsonProperty(EXAM.ATTR_END_TIME) final Long endTime,
            @JsonProperty(EXAM.ATTR_DELETION_TIME) final Long deletionTime) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.url = url;
        this.type = type;
        this.owner = owner;
        this.supporter = Utils.immutableCollectionOf(supporter);
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deletionTime = deletionTime;
        this.entityPrivileges = null;
    }

    public Exam(
            final Long id,
            final String uuid,
            final String name,
            final String description,
            final String url,
            final String type,
            final String owner,
            final Collection<String> supporter,
            final Long creationTime,
            final Long lastUpdateTime,
            final Long terminationTime,
            final Long startTime,
            final Long endTime, 
            final Long deletionTime,
            final Collection<EntityPrivilege> entityPrivileges) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.description = description;
        this.url = url;
        this.type = type;
        this.owner = owner;
        this.supporter = Utils.immutableCollectionOf(supporter);
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.deletionTime = deletionTime;
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
    public Long getId() {
        return this.id;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public EntityType entityType() {
        return EntityType.EXAM;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public String getUrl() {
        return this.url;
    }

    public String getType() {
        return this.type;
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

    public Long getStartTime() {
        return this.startTime;
    }

    public Long getEndTime() {
        return this.endTime;
    }

    @Override
    public Long getTerminationTime() {
        return this.terminationTime;
    }

    public String getOwner() {
        return owner;
    }

    public Collection<String> getSupporter() {
        return supporter;
    }

    public Long getDeletionTime() {
        return deletionTime;
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
        final Exam other = (Exam) obj;
        return Objects.equals(this.uuid, other.uuid);
    }

    @Override
    public String toString() {
        return "Exam{" +
                "id=" + id +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", owner='" + owner + '\'' +
                ", supporter=" + supporter +
                ", creationTime=" + creationTime +
                ", lastUpdateTime=" + lastUpdateTime +
                ", terminationTime=" + terminationTime +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", deletionTime=" + deletionTime +
                ", entityPrivileges=" + entityPrivileges +
                '}';
    }

    public static final Function<Collection<Exam>, List<Exam>> examSort(final String sort) {

        final String sortBy = PageSortOrder.decode(sort);
        return exams -> {
            final List<Exam> list = new ArrayList<>(exams);
            if (StringUtils.isBlank(sort)) {
                return list;
            }

            if (sortBy.equals(Exam.FILTER_ATTR_NAME)) {
                list.sort(Comparator.comparing(exam -> (exam.name != null) ? exam.name : StringUtils.EMPTY));
            }
            if (sortBy.equals(Exam.FILTER_ATTR_CREATION_TIME)) {
                list.sort(Comparator.comparing(exam -> (exam.creationTime != null) ? exam.creationTime : 0L));
            }

            if (PageSortOrder.DESCENDING == PageSortOrder.getSortOrder(sort)) {
                Collections.reverse(list);
            }
            return list;
        };
    }
}
