/*
 * Copyright (c) 2019 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.domain.model;

import java.util.Arrays;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import ch.ethz.seb.sps.domain.api.POSTMapper;
import ch.ethz.seb.sps.utils.Utils;

/** A Map containing various filter criteria from a certain API request.
 * This is used as a data object that can be used to collect API request parameter
 * data on one side and supply filter criteria based access to concrete Entity filtering
 * on the other side.
 *
 * All text based filter criteria are used as SQL wildcard's */
public class FilterMap extends POSTMapper {

    public FilterMap() {
        super(new LinkedMultiValueMap<>(), null);
    }

    public FilterMap(final MultiValueMap<String, String> params, final String uriQueryString) {
        super(params, uriQueryString);
    }

    public FilterMap(final HttpServletRequest request) {
        super(getRequestParams(request), null);
    }

    private static MultiValueMap<String, String> getRequestParams(final HttpServletRequest request) {
        final LinkedMultiValueMap<String, String> linkedMultiValueMap = new LinkedMultiValueMap<>();
        request.getParameterMap()
                .entrySet()
                .forEach(entry -> linkedMultiValueMap.put(entry.getKey(), Arrays.asList(entry.getValue())));
        return linkedMultiValueMap;
    }

    public boolean containsAny(final Set<String> extFilter) {
        return extFilter.stream()
                .filter(this.params::containsKey)
                .findFirst()
                .isPresent();
    }

    public Integer getActiveAsInt() {
        return getBooleanAsInteger(Entity.FILTER_ATTR_ACTIVE);
    }

    public String getSQLWildcard(final String name) {
        return Utils.toSQLWildcard(this.params.getFirst(name));
    }

    public static final class Builder {

        private final FilterMap filterMap = new FilterMap();

        public Builder add(final String name, final String value) {
            this.filterMap.params.add(name, value);
            return this;
        }

        public Builder put(final String name, final String value) {
            this.filterMap.params.put(name, Arrays.asList(value));
            return this;
        }

        public FilterMap create() {
            return new FilterMap(this.filterMap.params, null);
        }
    }

}
