/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.api;

import java.nio.CharBuffer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ch.ethz.seb.sps.utils.Utils;

/** A POST parameter mapper that wraps all parameter from a POST request given by a MultiValueMap<String, String> and
 * defines API specific convenience functions to access this parameter with given type and conversion of needed. */
public class POSTMapper {

    private static final Logger log = LoggerFactory.getLogger(POSTMapper.class);

    public static final POSTMapper EMPTY_MAP = new POSTMapper();

    protected final MultiValueMap<String, String> params;

    private POSTMapper() {
        super();
        this.params = new LinkedMultiValueMap<>();
    }

    public POSTMapper(final MultiValueMap<String, String> params, final String uriQueryString) {
        super();
        this.params = params != null
                ? new LinkedMultiValueMap<>(params)
                : new LinkedMultiValueMap<>();

        if (uriQueryString != null) {
            handleEncodedURIParams(uriQueryString);
        }
    }

    public POSTMapper(final Map<String, String[]> params, final String uriQueryString) {
        this(
                new LinkedMultiValueMap<>(params.entrySet().stream()
                        .collect(Collectors.toMap(e -> e.getKey(), e -> Arrays.asList(e.getValue())))),
                uriQueryString);

    }

    // NOTE: this is a workaround since URI parameter are not automatically decoded in the HTTPServletRequest
    //       while parameter from form-urlencoded body part are.
    //       I also tried to set application property: server.tomcat.uri-encoding=UTF-8 but with no effect.
    private void handleEncodedURIParams(final String uriQueryString) {
        final MultiValueMap<String, String> override = new LinkedMultiValueMap<>();
        this.params
                .entrySet()
                .stream()
                .forEach(entry -> {
                    if (uriQueryString.contains(entry.getKey())) {
                        override.put(
                                entry.getKey(),
                                entry.getValue().stream()
                                        .map(val -> decode(val))
                                        .collect(Collectors.toList()));
                    }
                });

        if (!override.isEmpty()) {
            this.params.putAll(override);
        }
    }

    private String decode(final String val) {
        try {
            return Utils.decodeFormURL_UTF_8(val);
        } catch (final Exception e) {
            return val;
        }
    }

    public String getString(final String name) {
        return this.params.getFirst(name);
    }

    public char[] getCharArray(final String name) {
        final String value = getString(name);
        if (value == null || value.length() <= 0) {
            return new char[] {};
        }

        return value.toCharArray();
    }

    public byte[] getBinary(final String name) {
        final String value = getString(name);
        if (value == null || value.length() <= 0) {
            return new byte[0];
        }

        return Utils.toByteArray(value);
    }

    public byte[] getBinaryFromBase64(final String name) {
        final String value = getString(name);
        if (value == null || value.length() <= 0) {
            return new byte[0];
        }

        return Base64.getDecoder().decode(value);
    }

    public CharSequence getCharSequence(final String name) {
        return CharBuffer.wrap(getCharArray(name));
    }

    public Long getLong(final String name) {
        final String value = this.params.getFirst(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        try {
            return Long.parseLong(value);
        } catch (final Exception e) {
            log.error("Failed to parse long value for attribute: {}", name, e.getMessage(), e);
            return null;
        }
    }

    public Short getShort(final String name) {
        final String value = this.params.getFirst(name);
        if (StringUtils.isBlank(value)) {
            return null;
        }

        return Short.parseShort(value);
    }

    public Integer getInteger(final String name) {
        final String value = this.params.getFirst(name);
        if (value == null) {
            return null;
        }

        return Integer.parseInt(value);
    }

    public Locale getLocale(final String name) {
        final String value = this.params.getFirst(name);
        if (value == null) {
            return null;
        }

        return Locale.forLanguageTag(value);
    }

    public boolean getBoolean(final String name) {
        return BooleanUtils.toBoolean(this.params.getFirst(name));
    }

    public Boolean getBooleanObject(final String name) {
        return BooleanUtils.toBooleanObject(this.params.getFirst(name));
    }

    public Integer getBooleanAsInteger(final String name) {
        final Boolean booleanObject = getBooleanObject(name);
        if (booleanObject == null) {
            return null;
        }
        return BooleanUtils.toIntegerObject(booleanObject);
    }

    public DateTimeZone getDateTimeZone(final String name) {
        final String value = this.params.getFirst(name);
        if (value == null) {
            return null;
        }
        try {
            return DateTimeZone.forID(value);
        } catch (final Exception e) {
            return null;
        }
    }

    public Set<String> getStringSet(final String name) {
        final List<String> list = this.params.get(name);
        if (list == null) {
            return Collections.emptySet();
        }
        return Utils.immutableSetOf(list);
    }

    public <T extends Enum<T>> T getEnum(final String name, final Class<T> type, final T defaultValue) {
        final T result = getEnum(name, type);
        if (result == null) {
            return defaultValue;
        }

        return result;
    }

    public <T extends Enum<T>> T getEnum(final String name, final Class<T> type) {
        final String value = this.params.getFirst(name);
        if (value == null) {
            return null;
        }
        try {
            return Enum.valueOf(type, value);
        } catch (final Exception e) {
            return null;
        }
    }

    public DateTime getDateTime(final String name) {
        final String value = this.params.getFirst(name);
        if (value == null) {
            return null;
        }

        return Utils.toDateTime(value);
    }

    public Map<String, String> getSubMap(final Set<String> actionAttributes) {
        return this.params
                .keySet()
                .stream()
                .filter(actionAttributes::contains)
                .collect(Collectors.toMap(Function.identity(), k -> this.params.getFirst(k)));
    }

    @SuppressWarnings("unchecked")
    public <T extends POSTMapper> T putIfAbsent(final String name, final String value) {
        this.params.putIfAbsent(name, Arrays.asList(value));
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public <T extends POSTMapper> T put(final String key, final String name) {
        this.params.put(key, Arrays.asList(name));
        return (T) this;
    }

    public String getUUID(String attributeName, boolean generate) {
        if (StringUtils.isBlank(attributeName)) {
            return generate ? UUID.randomUUID().toString() : null;
        }

        String uuid = getString(attributeName);
        if (StringUtils.isBlank(attributeName)) {
            return generate ? UUID.randomUUID().toString() : null;
        } else {
            try {
                return UUID.fromString(uuid).toString();
            } catch (final Exception e) {
                log.warn("Failed to parse UUID: {}", uuid);
                return generate ? UUID.randomUUID().toString() : null;
            }
        }
    }
}
