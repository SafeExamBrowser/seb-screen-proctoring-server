/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

//package ch.ethz.seb.sps.server.weblayer.oauth;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.ClientDetailsService;
//import org.springframework.security.oauth2.provider.ClientRegistrationException;
//import org.springframework.stereotype.Component;
//
//import ch.ethz.seb.sps.server.servicelayer.ClientAccessService;
//import ch.ethz.seb.sps.utils.Result;
//
///** A ClientDetailsService to manage different API clients of SEB Server webservice API.
// *
// * Currently supporting two client types for the two different API's on
// * SEB Server webservice;
// * - Administration API for administrative purpose using password grant type with refresh token
// * - Exam API for SEB-Client connections on running exams using client_credential grant type */
//@Lazy
//@Component
//public class SPSClientDetailsService implements ClientDetailsService {
//
//    private static final Logger log = LoggerFactory.getLogger(SPSClientDetailsService.class);
//
//    private final ClientAccessService clientAccessService;
//    private final GUIClientAPIClientDetails guiClientAPIClientDetails;
//    private final SEBServerAPIClientDetails sebServerAPIClientDetails;
//
//    public SPSClientDetailsService(
//            final GUIClientAPIClientDetails guiClientAPIClientDetails,
//            final ClientAccessService clientAccessService,
//            final SEBServerAPIClientDetails sebServerAPIClientDetails) {
//
//        this.guiClientAPIClientDetails = guiClientAPIClientDetails;
//        this.clientAccessService = clientAccessService;
//        this.sebServerAPIClientDetails = sebServerAPIClientDetails;
//    }
//
//    /** Load a client by the client id. This method must not return null.
//     *
//     * This checks first if the given clientId matches the client id of AdminAPIClientDetails.
//     * If not, iterating through LMSSetup's and matches the sebClientname of each LMSSetup.
//     * If there is a match, a ClientDetails object is created from LMSSetup and returned.
//     * If there is no match at all, a ClientRegistrationException is thrown
//     *
//     * @param clientId The client id.
//     * @return The client details (never null).
//     * @throws ClientRegistrationException If the client account is locked, expired, disabled, or invalid for any other
//     *             reason. */
//    @Override
//    public ClientDetails loadClientByClientId(final String clientId) throws ClientRegistrationException {
//        if (clientId == null) {
//            throw new ClientRegistrationException("clientId is null");
//        }
//
//        if (clientId.equals(this.guiClientAPIClientDetails.getClientId())) {
//            return this.guiClientAPIClientDetails;
//        }
//        if (clientId.equals(this.sebServerAPIClientDetails.getClientId())) {
//            return this.sebServerAPIClientDetails;
//        }
//
//        return getForClientAPI(clientId)
//                .get(t -> {
//                    log.warn("Active client not found: {} cause: {}", clientId, t.getMessage());
//                    throw new UsernameNotFoundException(t.getMessage());
//                });
//    }
//
//    protected Result<ClientDetails> getForClientAPI(final String clientId) {
//        return this.clientAccessService.getClientDetails(clientId);
//    }
//
//}
