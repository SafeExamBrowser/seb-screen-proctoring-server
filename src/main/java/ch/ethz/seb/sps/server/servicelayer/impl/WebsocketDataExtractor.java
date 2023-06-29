/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.utils.Utils;

@Lazy
@Component
public class WebsocketDataExtractor {

    private static final Logger log = LoggerFactory.getLogger(WebsocketDataExtractor.class);

    private final JSONMapper jsonMapper;

    public WebsocketDataExtractor(final JSONMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Async(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR)
    public void storeScreenshot(
            final String sessionUUID,
            final InputStream in,
            final ScreenshotStoreService storeService) {

        try {

            // get fist two bytes with meta date length
            final byte[] lBytes = new byte[2];
            in.read(lBytes);
            final short metaLengh = ByteBuffer.wrap(lBytes).getShort(); // big-endian by default

            // now get the meta data
            final byte[] meta = new byte[metaLengh];
            in.read(meta);
            final String metaDataJson = Utils.toString(meta);

            final HashMap<String, String> metaDataMap = this.jsonMapper.readValue(
                    metaDataJson,
                    new TypeReference<HashMap<String, String>>() {
                    });

            final Long timestamp = Long.valueOf(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP));
            final ImageFormat format = ImageFormat.byName(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT));

            storeService.storeScreenshot(
                    sessionUUID,
                    timestamp,
                    format,
                    this.jsonMapper.writeValueAsString(metaDataMap),
                    in);

        } catch (final Exception e) {
            log.error("Failed to store screenshot: ", e);
        }
    }

}
