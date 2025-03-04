/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.server.datalayer.dao.AdditionalAttributesDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.server.servicelayer.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
public class ScreenshotUploadServiceImpl {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceImpl.class);
    
    private final AdditionalAttributesDAO additionalAttributesDAO;
    private final SessionDAO sessionDAO;
    private final ScreenshotDataDAO screenshotDataDAO;

    public ScreenshotUploadServiceImpl(
            final AdditionalAttributesDAO additionalAttributesDAO,
            final SessionService sessionService, SessionDAO sessionDAO, 
            final ScreenshotDataDAO screenshotDataDAO) {
        
        this.additionalAttributesDAO = additionalAttributesDAO;
        this.sessionDAO = sessionDAO;
        this.screenshotDataDAO = screenshotDataDAO;
    }

    @EventListener(SessionOnClosingEvent.class)
    public void onSessionClose(SessionOnClosingEvent event) {
        try {
            additionalAttributesDAO
                    .getAdditionalAttributes(
                            EntityType.SESSION,
                            sessionDAO.modelIdToPK(event.sessionUUID))
                    .getOrThrow()
                    .stream()
                    .filter( attr -> attr.getName().startsWith(AdditionalAttributesDAO.ATTRIBUTE_SESSION_ALSO_CLOSE))
                    .forEach(attr -> {

                        final String sessionUUID = attr.getValue();
                        this.screenshotDataDAO
                                .getLatest(sessionUUID)
                                .flatMap( latest -> sessionDAO.closeAt(sessionUUID, latest.getTimestamp()))
                                .onError( error -> log.error("Failed to close session from upload: {} error: {}", sessionUUID, error.getMessage()))
                                .onSuccess( id -> additionalAttributesDAO.delete(attr.getId()));
                    });

        } catch (Exception e) {
            log.error("Failed to close sessions from upload: {} error: ", event.sessionUUID, e);
        }
    }
}
