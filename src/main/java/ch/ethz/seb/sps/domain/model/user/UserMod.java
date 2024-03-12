/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model.user;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import ch.ethz.seb.sps.domain.Domain.USER;
import ch.ethz.seb.sps.domain.api.API.UserRole;
import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.EntityType;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class UserMod implements UserAccount {

    public static final String ATTR_USER_ROLES = "userRoles";

    @JsonProperty(USER.ATTR_UUID)
    public final String uuid;

    /** first (or full) name of the user */
    @NotNull(message = "user:name:notNull")
    @Size(max = 255, message = "user:name:size:{min}:{max}:${validatedValue}")
    @JsonProperty(USER.ATTR_NAME)
    public final String name;

    /** surname of the user */
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

    /** The users roles in a unmodifiable set */
    @NotNull(message = "user:userRoles:notNull")
    @NotEmpty(message = "user:userRoles:notNull")
    @JsonProperty(ATTR_USER_ROLES)
    public final Set<String> roles;

    @NotNull(message = "user:newPassword:notNull")
    @Size(min = 8, max = 255, message = "user:newPassword:size:{min}:{max}:${validatedValue}")
    @JsonProperty(PasswordChange.ATTR_NAME_NEW_PASSWORD)
    private final CharSequence newPassword;

    @NotNull(message = "user:confirmNewPassword:notNull")
    @JsonProperty(PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD)
    private final CharSequence confirmNewPassword;

    @JsonCreator
    public UserMod(
            @JsonProperty(USER.ATTR_UUID) final String uuid,
            @JsonProperty(USER.ATTR_NAME) final String name,
            @JsonProperty(USER.ATTR_SURNAME) final String surname,
            @JsonProperty(USER.ATTR_USERNAME) final String username,
            @JsonProperty(PasswordChange.ATTR_NAME_NEW_PASSWORD) final CharSequence newPassword,
            @JsonProperty(PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD) final CharSequence confirmNewPassword,
            @JsonProperty(USER.ATTR_EMAIL) final String email,
            @JsonProperty(USER.ATTR_LANGUAGE) final Locale language,
            @JsonProperty(USER.ATTR_TIMEZONE) final DateTimeZone timeZone,
            @JsonProperty(ATTR_USER_ROLES) final Set<String> roles) {

        this.uuid = uuid;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
        this.name = name;
        this.surname = surname;
        this.username = username;
        this.email = email;
        this.language = (language != null) ? language : Locale.ENGLISH;
        this.timeZone = (timeZone != null) ? timeZone : DateTimeZone.UTC;
        this.roles = (roles != null)
                ? Collections.unmodifiableSet(roles)
                : Collections.emptySet();
    }

    public UserMod(final String modelId, final POSTMapper postAttrMapper) {
        this.uuid = modelId;
        this.newPassword = postAttrMapper.getString(PasswordChange.ATTR_NAME_NEW_PASSWORD);
        this.confirmNewPassword = postAttrMapper.getString(PasswordChange.ATTR_NAME_CONFIRM_NEW_PASSWORD);
        this.name = postAttrMapper.getString(USER.ATTR_NAME);
        this.surname = postAttrMapper.getString(USER.ATTR_SURNAME);
        this.username = postAttrMapper.getString(USER.ATTR_USERNAME);
        this.email = postAttrMapper.getString(USER.ATTR_EMAIL);
        this.language = postAttrMapper.getLocale(USER.ATTR_LANGUAGE);
        this.timeZone = postAttrMapper.getDateTimeZone(USER.ATTR_TIMEZONE);
        this.roles = postAttrMapper.getStringSet(USER.ATTR_ROLES);
    }

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public String getModelId() {
        return this.uuid;
    }

    @Override
    public EntityType entityType() {
        return EntityType.USER;
    }

    @Override
    public DateTime getCreationDate() {
        return null;
    }

    public CharSequence getNewPassword() {
        return this.newPassword;
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

    public CharSequence getRetypedNewPassword() {
        return this.confirmNewPassword;
    }

    public boolean passwordChangeRequest() {
        return this.newPassword != null;
    }

    public boolean newPasswordMatch() {
        return passwordChangeRequest() && this.newPassword.equals(this.confirmNewPassword);
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    @JsonIgnore
    public EnumSet<UserRole> getUserRoles() {
        return EnumSet.copyOf(
                getRoles().stream()
                        .map(UserRole::valueOf)
                        .collect(Collectors.toList()));
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
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UserMod [uuid=");
        builder.append(this.uuid);
        builder.append(", name=");
        builder.append(this.name);
        builder.append(", surname=");
        builder.append(this.surname);
        builder.append(", username=");
        builder.append(this.username);
        builder.append(", email=");
        builder.append(this.email);
        builder.append(", language=");
        builder.append(this.language);
        builder.append(", timeZone=");
        builder.append(this.timeZone);
        builder.append(", roles=");
        builder.append(this.roles);
        builder.append(", newPassword=");
        builder.append(this.newPassword);
        builder.append(", confirmNewPassword=");
        builder.append(this.confirmNewPassword);
        builder.append("]");
        return builder.toString();
    }

    public static UserMod createNew(final Long institutionId) {
        return new UserMod(
                UUID.randomUUID().toString(),
                null, null, null, null, null, null, null, null, null);
    }

}
