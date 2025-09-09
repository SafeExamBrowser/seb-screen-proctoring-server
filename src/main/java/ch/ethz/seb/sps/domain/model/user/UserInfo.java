/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.USER;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityName;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.OwnedEntity;
import ch.ethz.seb.sps.domain.model.WithEntityPrivileges;
import ch.ethz.seb.sps.domain.model.WithLifeCycle;
import ch.ethz.seb.sps.utils.Utils;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.AccessMode;

/** The user info domain model contains primary user information
 * <p>
 * This domain model is annotated and fully serializable and deserializable
 * to and from JSON within the Jackson library.
 * <p>
 * This domain model is immutable and thread-save */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserInfo implements UserAccount, OwnedEntity, WithEntityPrivileges, WithLifeCycle, Serializable {

    private static final long serialVersionUID = 4517645738787224836L;

    public static final String FILTER_ATTR_SURNAME = "surname";
    public static final String FILTER_ATTR_USER_NAME = "username";
    public static final String FILTER_ATTR_EMAIL = "email";
    public static final String FILTER_ATTR_LANGUAGE = "language";

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonProperty(USER.ATTR_ID)
    public final Long id;

    /** The user's UUID */
    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(USER.ATTR_UUID)
    public final String uuid;

    /** First name of the user */
    @NotNull(message = "user:name:notNull")
    @Size(max = 255, message = "user:name:size:{min}:{max}:${validatedValue}")
    @JsonProperty(USER.ATTR_NAME)
    public final String name;

    /** Surname of the user */
    @NotNull(message = "user:surname:notNull")
    @Size(max = 255, message = "user:surname:size:{min}:{max}:${validatedValue}")
    @JsonProperty(USER.ATTR_SURNAME)
    public final String surname;

    /** The internal user name */
    @NotNull(message = "user:username:notNull")
    @Size(min = 3, max = 255, message = "user:username:size:{min}:{max}:${validatedValue}")
    @JsonProperty(USER.ATTR_USERNAME)
    public final String username;

    /** E-mail address of the user */
    @Email(message = "user:email:email:_:_:${validatedValue}")
    @JsonProperty(USER.ATTR_EMAIL)
    public final String email;

    /** The users locale */
    @NotNull(message = "user:language:notNull")
    @JsonProperty(USER.ATTR_LANGUAGE)
    public final Locale language;

    /** The users time zone */
    @NotNull(message = "user:timeZone:notNull")
    @JsonProperty(USER.ATTR_TIMEZONE)
    public final DateTimeZone timeZone;

    /** The users roles in a unmodifiable set. Is never null */
    @Schema(accessMode = AccessMode.READ_ONLY)
    @NotNull(message = "user:userRoles:notNull")
    @NotEmpty(message = "user:userRoles:notNull")
    @JsonProperty(USER.ATTR_ROLES)
    public final Set<String> roles;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(USER.ATTR_CREATION_TIME)
    public final Long creationTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(USER.ATTR_LAST_UPDATE_TIME)
    public final Long lastUpdateTime;

    @Schema(accessMode = AccessMode.READ_ONLY)
    @JsonProperty(USER.ATTR_TERMINATION_TIME)
    public final Long terminationTime;

    @JsonIgnore
    public final Collection<EntityPrivilege> entityPrivileges;

    @JsonCreator
    public UserInfo(
            @JsonProperty(USER.ATTR_ID) final Long id,
            @JsonProperty(USER.ATTR_UUID) final String uuid,
            @JsonProperty(USER.ATTR_NAME) final String name,
            @JsonProperty(USER.ATTR_SURNAME) final String surname,
            @JsonProperty(USER.ATTR_USERNAME) final String username,
            @JsonProperty(USER.ATTR_EMAIL) final String email,
            @JsonProperty(USER.ATTR_LANGUAGE) final Locale language,
            @JsonProperty(USER.ATTR_TIMEZONE) final DateTimeZone timeZone,
            @JsonProperty(USER.ATTR_ROLES) final Set<String> roles,
            @JsonProperty(USER.ATTR_CREATION_TIME) final Long creationTime,
            @JsonProperty(USER.ATTR_LAST_UPDATE_TIME) final Long lastUpdateTime,
            @JsonProperty(USER.ATTR_TERMINATION_TIME) final Long terminationTime) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.language = language;
        this.timeZone = timeZone;
        this.roles = Utils.immutableSetOf(roles);
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.entityPrivileges = null;
    }

    public UserInfo(
            final Long id,
            final String uuid,
            final String name,
            final String surname,
            final String username,
            final String email,
            final Locale language,
            final DateTimeZone timeZone,
            final Set<String> roles,
            final Long creationTime,
            final Long lastUpdateTime,
            final Long terminationTime,
            final Collection<EntityPrivilege> entityPrivileges) {

        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.language = language;
        this.timeZone = timeZone;
        this.roles = Utils.immutableSetOf(roles);
        this.creationTime = creationTime;
        this.lastUpdateTime = lastUpdateTime;
        this.terminationTime = terminationTime;
        this.entityPrivileges = Utils.immutableCollectionOf(entityPrivileges);
    }

    @Override
    public String getOwnerId() {
        return this.uuid;
    }

    @Override
    public Collection<EntityPrivilege> getEntityPrivileges() {
        return this.entityPrivileges;
    }

    @Override
    public DateTime getCreationDate() {
        return Utils.toDateTimeUTC(this.creationTime);
    }

    @Override
    public EntityType entityType() {
        return EntityType.USER;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public String getModelId() {
        return this.uuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getSurname() {
        return this.surname;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @JsonIgnore
    @Override
    public boolean isActive() {
        return this.terminationTime == null;
    }

    @Override
    public Locale getLanguage() {
        return this.language;
    }

    @Override
    public DateTimeZone getTimeZone() {
        return this.timeZone;
    }

    @Override
    public Set<String> getRoles() {
        return this.roles;
    }

    @Override
    @JsonIgnore
    public Set<UserRole> getUserRoles() {
        return EnumSet.copyOf(
                getRoles().stream()
                        .map(UserRole::valueOf)
                        .collect(Collectors.toList()));
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

    @JsonIgnore
    @Override
    public EntityKey getEntityKey() {
        if (StringUtils.isBlank(this.uuid)) {
            return null;
        }
        return new EntityKey(this.uuid, entityType());
    }

    @Override
    public EntityName toName() {
        return new EntityName(
                this.getModelId(),
                this.entityType(),
                this.getUsername() + " (" + this.getSurname() + " " + this.getName() + ")");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.uuid == null) ? 0 : this.uuid.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final UserInfo other = (UserInfo) obj;
        if (this.uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!this.uuid.equals(other.uuid))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UserInfo [uuid=" + this.uuid +
                ", name=" + this.name +
                ", surname=" + this.surname +
                ", username=" + this.username +
                ", email=" + this.email +
                ", language=" + this.language +
                ", timeZone=" + this.timeZone +
                ", roles=" + this.roles +
                ", creationTime=" + this.creationTime +
                ", lastUpdateTime=" + this.lastUpdateTime +
                ", terminationTime=" + this.terminationTime +
                "]";
    }

    /** Use this to create a copy of a given UserInfo instance.
     *
     * @param userInfo UserInfo instance to copy
     * @return copied UserInfo instance */
    public static UserInfo of(final UserInfo userInfo) {
        return new UserInfo(
                userInfo.id,
                userInfo.uuid,
                userInfo.name,
                userInfo.username,
                userInfo.surname,
                userInfo.email,
                userInfo.language,
                userInfo.timeZone,
                userInfo.roles,
                userInfo.creationTime,
                userInfo.lastUpdateTime,
                userInfo.terminationTime,
                userInfo.entityPrivileges);
    }

}
