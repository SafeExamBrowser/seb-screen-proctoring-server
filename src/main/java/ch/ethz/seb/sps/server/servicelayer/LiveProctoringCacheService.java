/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer;

import java.util.Collection;

import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.servicelayer.impl.ScreenshotQueueData;
import ch.ethz.seb.sps.utils.Result;
import org.springframework.context.event.EventListener;

public interface LiveProctoringCacheService {

    @EventListener(ServiceInitEvent.class)
    void init();
    
    /** Get the PK id of the last screenshot_data row for a given live session.
     * 
     * @param sessionUUID The live session UUID
     * @return PK id of the last screenshot_data row if available or -1 if there is no screenshot yet or null if there is no slot for the given sessionUUID*/
    Long getLatestSSDataId(String sessionUUID);

    /** Creates a new cache slot for given session. Usually called when session is created
     *
     * @param sessionUUID Session UUID to create a live cache slot for
     * @return Result refer to given sessionUUID or to an error when happened */
    Result<String> createCacheSlot(String sessionUUID);

    /** Deletes a cache slot for a given session. Usually called when session is closed
     *
     * @param sessionUUID Session UUID to create a live cache slot for
     * @return Result refer to given sessionUUID or to an error when happened */
    Result<String> deleteCacheSlot(String sessionUUID);
    
    /** Called by the batch store services to update latest cache entries on storage
     * @param batch The batch with the latest screenshot_data ids */
    void updateCacheStore(Collection<ScreenshotQueueData> batch);

    /** Goes through all cache slots and deletes the one that has a closed session */
    void cleanup();
}
