/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.io.InputStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.datalayer.batis.ScreenshotMapper;
import ch.ethz.seb.sps.server.datalayer.batis.ScreenshotMapper.BlobContent;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FULL_RDBMS')")
public class ScreenshotDAOBatis implements ScreenshotDAO {

    private final ScreenshotMapper screenshotMapper;

    public ScreenshotDAOBatis(final ScreenshotMapper screenshotMapper) {
        this.screenshotMapper = screenshotMapper;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot FULL_RDBMS Store: initialized");
    }

    @Override
    @Transactional(readOnly = true)
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionId) {

        return Result.tryCatch(() -> {
            return this.screenshotMapper
                    .selectScreenshotByPK(pk)
                    .getImage();
        });
    }

    @Override
    @Transactional
    public Result<Long> storeImage(
            final Long pk,
            final String sessionId,
            final InputStream in) {

        return Result.tryCatch(() -> {
            final BlobContent blobContent = new BlobContent(pk, in);
            this.screenshotMapper.insert(blobContent);
            return blobContent.getId();
        });
    }

}
