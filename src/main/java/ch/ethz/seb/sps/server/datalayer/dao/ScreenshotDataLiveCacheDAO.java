/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.List;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataLiveCacheRecord;
import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDataLiveCacheDAO {
    
    Result<ScreenshotDataLiveCacheRecord> createCacheEntry(String sessionUUID);

    Result<String> deleteCacheEntry(String sessionUUID);
    
    Result<List<String>> deleteAll(List<String> sessionUUIDs);

    Result<Collection<ScreenshotDataLiveCacheRecord>> getAll();
}

