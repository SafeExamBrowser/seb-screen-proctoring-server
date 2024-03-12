/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;
import ch.ethz.seb.sps.utils.Constants;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
public class WebsocketScreenshotMessageHandler extends BinaryWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebsocketScreenshotMessageHandler.class);

    private final ScreenshotStoreService screenshotStoreService;
    private final SessionServiceHealthControl sessionServiceHealthControl;

    private final Map<String, ScreenshotSession> sessions = new ConcurrentHashMap<>();

    public WebsocketScreenshotMessageHandler(
            final ScreenshotStoreService screenshotStoreService,
            final SessionServiceHealthControl sessionServiceHealthControl) {

        this.screenshotStoreService = screenshotStoreService;
        this.sessionServiceHealthControl = sessionServiceHealthControl;
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @EventListener
    public void sessionOnClose(final SessionOnClosingEvent event) {
        this.sessions.values().stream().filter(session -> event.sessionUUID.equals(session.sessionUUID)).findFirst()
                .ifPresent(session -> {
                    this.sessions.remove(session.getWebsocketSessionId());
                    session.closeWebSocketSession();
                });
    }

    @Override
    public void afterConnectionEstablished(final WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);

        final String sessionUUID = session.getHandshakeHeaders().getFirst(API.SESSION_HEADER_UUID);

        if (StringUtils.isBlank(sessionUUID)) {
            log.error("Websocket connection request with missing mandatory SEB_SESSION_UUID header! Close session.");
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        this.sessions.put(session.getId(), new ScreenshotSession(sessionUUID, session));

        if (log.isDebugEnabled()) {
            log.debug("New websocket connection established for session: {}", sessionUUID);
        }
    }

    @Override
    public void afterConnectionClosed(final WebSocketSession session, final CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);

        this.sessions.remove(session.getId());

        if (log.isDebugEnabled()) {
            final String sessionUUID = session.getHandshakeHeaders().getFirst(API.SESSION_HEADER_UUID);
            log.debug("Websocket connection closed for session: {}", sessionUUID);
        }
    }

    @Override
    public void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) {

        final String websocketSessionId = session.getId();

        if (log.isDebugEnabled()) {
            log.debug("Binary message for web-socket session: {} received. Message length: {}, is last: {}",
                    websocketSessionId,
                    message.getPayloadLength(),
                    message.isLast());
        }

        final ScreenshotSession screenshotSession = this.sessions.get(websocketSessionId);
        if (screenshotSession == null) {

            log.error("Failed to find ScreenshotSession for web-socket session: {} with sessionUUID: {}",
                    session.getId(),
                    session.getHandshakeHeaders().getFirst(API.SESSION_HEADER_UUID));

            try {
                session.close(CloseStatus.SESSION_NOT_RELIABLE);
            } catch (final IOException e) {
                log.error("Failed to close session: ", e);
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("ScreenshotSession for session: {} found.", screenshotSession.sessionUUID);
        }

        screenshotSession.notifyMessage(message);
        pushServerHealth(session);
    }

    private class ScreenshotSession {

        final String sessionUUID;

        private final WebSocketSession webSocketSession;
        private PipedOutputStream out = null;
        private boolean runningTransaction = false;

        public ScreenshotSession(final String sessionUUID, final WebSocketSession webSocketSession) {
            this.sessionUUID = sessionUUID;
            this.webSocketSession = webSocketSession;
        }

        public Object getWebsocketSessionId() {
            return this.webSocketSession.getId();
        }

        void notifyMessage(final BinaryMessage message) {

            if (!this.runningTransaction) {
                startTransaction(message);
            } else {
                streamImageData(message);
            }
            if (message.isLast()) {
                closeTransaction();
            }

        }

        void closeWebSocketSession() {
            try {
                this.webSocketSession.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }

        private void startTransaction(final BinaryMessage message) {

            if (log.isDebugEnabled()) {
                log.debug(" *** start screenshot transaction for session: {}", this.sessionUUID);
            }

            this.runningTransaction = true;
            this.out = new PipedOutputStream();

            try {

                final PipedInputStream in = new PipedInputStream(this.out, 51200);
                WebsocketScreenshotMessageHandler.this.screenshotStoreService
                        .storeScreenshot(this.sessionUUID, in);

                streamImageData(message);

            } catch (final Exception e) {
                log.error("Failed to start screenshot upstream websocket transaction:", e);
                log.info("Close open screenshot transaction...");
                closeTransaction();
            }
        }

        private void streamImageData(final BinaryMessage message) {
            try {

                log.info("--> streamImageData");

                this.out.write(Utils.toByteArray(message.getPayload()));
            } catch (final IOException e) {
                log.error("Failed to stream data to open transaction: ", e);
                log.info("Close open screenshot transaction...");
                closeTransaction();
            }
        }

        private void closeTransaction() {

            if (log.isDebugEnabled()) {
                log.debug(" *** close screenshot transaction for session: {}", this.sessionUUID);
            }

            try {
                this.out.close();
            } catch (final Exception e) {
                log.error("Failed to close screenshot upstream websocket transaction:", e);
            } finally {
                IOUtils.closeQuietly(this.out);
                this.out = null;
                this.runningTransaction = false;
            }
        }
    }

    private void pushServerHealth(final WebSocketSession session) {
        final int overalLoadIndicator = this.sessionServiceHealthControl.getOverallLoadIndicator();
        if (overalLoadIndicator > 0) {
            try {
                session.sendMessage(
                        new TextMessage(API.SPS_SERVER_HEALTH + Constants.EQUALITY_SIGN + overalLoadIndicator));
            } catch (final IOException e) {
                log.error("Failed to send server health indicator message to client for session: {}",
                        session.getId(),
                        e);
            }
        }
    }

}
