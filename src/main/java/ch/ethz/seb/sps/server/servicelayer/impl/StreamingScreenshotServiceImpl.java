/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.utils.Utils;

@Service
@ConditionalOnExpression("'${sps.data.store.strategy}'.equals('SINGLE_STREAMING')")
public class StreamingScreenshotServiceImpl implements ScreenshotStoreService {

    private static final Logger log = LoggerFactory.getLogger(StreamingScreenshotServiceImpl.class);

    private final ScreenshotDAO screenshotDAO;
    private final ScreenshotDataDAO screenshotDataDAO;
    private final JSONMapper jsonMapper;

    public StreamingScreenshotServiceImpl(
            final ScreenshotDAO screenshotDAO,
            final ScreenshotDataDAO screenshotDataDAO,
            final JSONMapper jsonMapper) {

        this.screenshotDAO = screenshotDAO;
        this.screenshotDataDAO = screenshotDataDAO;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot Store Strategy SINGLE_STREAMING: initialized");
    }

    @Override
    public int getStoreHealthIndicator() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void storeScreenshot(final String sessionUUID, final InputStream in) {

        try {

            // get fist two bytes with meta date length
            final byte[] lBytes = new byte[2];
            in.read(lBytes);
            final short metaLengh = ByteBuffer.wrap(lBytes).getShort(); // big-endian by default

            System.out.println("******** metaLengh: " + metaLengh);

            // now get the meta data
            final byte[] meta = new byte[metaLengh];
            in.read(meta);
            final String metaDataJson = Utils.toString(meta);

            System.out.println("******** metaDataJson: " + metaDataJson);

            final HashMap<String, String> metaDataMap = this.jsonMapper.readValue(
                    metaDataJson,
                    new TypeReference<HashMap<String, String>>() {
                    });

            final Long timestamp = Long.valueOf(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_TIMESTAMP));
            final ImageFormat format = ImageFormat.byName(metaDataMap.remove(Domain.SCREENSHOT_DATA.ATTR_IMAGE_FORMAT));

            storeScreenshot(sessionUUID, timestamp, format, this.jsonMapper.writeValueAsString(metaDataMap), in);

        } catch (final Exception e) {
            log.error("Failed to store screenshot: ", e);
        }
    }

    @Override
    public void storeScreenshot(
            final String sessionUUID,
            final Long timestamp,
            final ImageFormat imageFormat,
            final String metadata,
            final InputStream in) {

        this.screenshotDataDAO
                .save(sessionUUID, timestamp, imageFormat, metadata)
                .onSuccess(id -> this.screenshotDAO
                        .storeImage(id, sessionUUID, in)
                        .onError(error -> log.error("Failed to store screen shot: ", error)))
                .onError(error -> log.error("Failed to store screen shot data: ", error));
    }

}
