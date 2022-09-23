/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.provider.ClientDetails;

import ch.ethz.seb.sps.utils.Result;

public interface ClientAccessService {

    Logger log = LoggerFactory.getLogger(ClientAccessService.class);

    /** The cache name of ClientDetails */
    String EXAM_CLIENT_DETAILS_CACHE = "EXAM_CLIENT_DETAILS_CACHE";

    /** Get the ClientDetails for given client name that identifies a SEBClientConfiguration entry.
     *
     * @param clientName the client name of a SEBClientConfiguration entry
     * @return Result refer to the ClientDetails for the specified clientName or to an error if happened */
    @Cacheable(
            cacheNames = EXAM_CLIENT_DETAILS_CACHE,
            key = "#clientName",
            unless = "#result.hasError()")
    Result<ClientDetails> getClientConfigDetails(String clientName);

}
