/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.weblayer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import ch.ethz.seb.sps.domain.api.API;

@Configuration
@EnableWebSocket
public class WebsocketConfig implements WebSocketConfigurer {

    @Value("${sps.api.session.endpoint.v1}")
    private String sessionAPIEndpoint;
    @Autowired
    private WebsocketScreenshotMessageHandler websocketScreenshotMessageHandler;

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        final ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxBinaryMessageBufferSize(200000);
        return container;
    }

    @Override
    public void registerWebSocketHandlers(final WebSocketHandlerRegistry registry) {
        final String dataURL = this.sessionAPIEndpoint + API.WEBSOCKET_SESSION_ENDPOINT;

        final DefaultHandshakeHandler defaultHandshakeHandler =
                new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy());

        registry
                .addHandler(this.websocketScreenshotMessageHandler, dataURL)
                .setHandshakeHandler(defaultHandshakeHandler)
                .setAllowedOrigins("*");
    }

}
