/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

import ch.ethz.seb.sps.utils.Result;
import org.springframework.security.oauth2.provider.ClientDetails;

public interface ClientAccessService {

    Logger log = LoggerFactory.getLogger(ClientAccessService.class);

    /** The cache name of ClientDetails */
    String SEB_CLIENT_DETAILS_CACHE = "SEB_CLIENT_DETAILS_CACHE";

    /** Get the ClientDetails for given client name that identifies a SEBClientConfiguration entry.
     *
     * @param clientName the client name of a SEBClientConfiguration entry
     * @return Result refer to the ClientDetails for the specified clientName or to an error if happened */
    @Cacheable(
            cacheNames = SEB_CLIENT_DETAILS_CACHE,
            key = "#clientName",
            unless = "#result.hasError()")
    Result<ClientDetails> getClientDetails(String clientName);

}
