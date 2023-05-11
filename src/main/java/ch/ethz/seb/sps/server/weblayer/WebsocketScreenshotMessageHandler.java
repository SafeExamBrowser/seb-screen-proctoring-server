/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
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
import java.util.concurrent.CompletableFuture;
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
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.screenshot.ScreenshotData;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotService;
import ch.ethz.seb.sps.server.servicelayer.SessionOnClosingEvent;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
public class WebsocketScreenshotMessageHandler extends BinaryWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(WebsocketScreenshotMessageHandler.class);

    private final ScreenshotService screenshotService;
    private final JSONMapper jsonMapper;

    private final Map<String, ScreenshotSession> sessions = new ConcurrentHashMap<>();

    public WebsocketScreenshotMessageHandler(
            final ScreenshotService screenshotService,
            final JSONMapper jsonMapper) {

        this.screenshotService = screenshotService;
        this.jsonMapper = jsonMapper;
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
    }

    private class ScreenshotSession {

        final String sessionUUID;

        private final WebSocketSession webSocketSession;
        private CompletableFuture<Void> future = null;
        private PipedOutputStream out = null;
        private ScreenshotData screenshotData;

        public ScreenshotSession(final String sessionUUID, final WebSocketSession webSocketSession) {
            this.sessionUUID = sessionUUID;
            this.webSocketSession = webSocketSession;
        }

        public Object getWebsocketSessionId() {
            return this.webSocketSession.getId();
        }

        void notifyMessage(final BinaryMessage message) {
            if (message.isLast()) {
                if (this.future == null) {
                    startTransaction(message);
                } else {
                    try {
                        this.out.write(Utils.toByteArray(message.getPayload()));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                    closeTransaction();
                }
            } else {
                if (this.future == null || this.out == null) {
                    log.error("Expected to be in streaming state but not!");
                } else {
                    try {
                        this.out.write(Utils.toByteArray(message.getPayload()));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                }
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

            log.debug(" *** start screenshot transaction for session: {}", this.sessionUUID);

            this.future = new CompletableFuture<>();
            this.out = new PipedOutputStream();

            try {
                final PipedInputStream in = new PipedInputStream(this.out, 51200);

                this.screenshotData = getMetaData(message);

                WebsocketScreenshotMessageHandler.this.screenshotService.storeScreenshot(
                        this.sessionUUID,
                        this.screenshotData.timestamp,
                        this.screenshotData.imageFormat,
                        this.screenshotData.metaData,
                        in,
                        this.future);

            } catch (final Exception e) {
                log.error("Failed to start screenshot upstream websocket transaction:", e);
                IOUtils.closeQuietly(this.out);
                this.out = null;
                this.future = null;
            }
        }

        private ScreenshotData getMetaData(final BinaryMessage message) {
            try {
                final String metaDataJson = Utils.toString(message.getPayload());
                return WebsocketScreenshotMessageHandler.this.jsonMapper.readValue(metaDataJson, ScreenshotData.class);
            } catch (final Exception e) {
                log.error("Failed to read screenshot meta-data");
                throw new RuntimeException("Failed to read screenshot meta-data:", e);
            }
        }

        private void closeTransaction() {

            log.debug(" *** close screenshot transaction for session: {}", this.sessionUUID);

            try {
                this.out.close();
                this.future.get();
            } catch (final Exception e) {
                log.error("Failed to close screenshot upstream websocket transaction:", e);
            } finally {
                IOUtils.closeQuietly(this.out);
                this.out = null;
                this.future = null;
            }
        }
    }

}
