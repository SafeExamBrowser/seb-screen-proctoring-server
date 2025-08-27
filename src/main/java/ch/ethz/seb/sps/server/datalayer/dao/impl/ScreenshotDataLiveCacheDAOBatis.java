/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataLiveCacheDAO;
import ch.ethz.seb.sps.utils.Result;
import org.springframework.stereotype.Service;

@Service
public class ScreenshotDataLiveCacheDAOBatis implements ScreenshotDataLiveCacheDAO {
    
    @Override
    public Result<ScreenshotDataLiveCacheRecord> createCacheEntry(String sessionUUID) {
        return null;
//        return Result.tryCatch(() -> {
//            new ScreenshotDataLiveCacheRecord( null, sessionUUID, -1L );
//        });
    }

    @Override
    public Result<String> deleteCacheEntry(String sessionUUID) {
        return null;
    }

    @Override
    public Result<List<String>> deleteAll(List<String> sessionUUIDs) {
        return null;
    }

    @Override
    public Result<Collection<ScreenshotDataLiveCacheRecord>> getAll() {
        return null;
    }
}
