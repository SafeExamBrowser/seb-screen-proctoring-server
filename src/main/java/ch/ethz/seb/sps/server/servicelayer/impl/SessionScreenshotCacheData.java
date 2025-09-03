/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;

public class SessionScreenshotCacheData {
    
    public final String sessionUUID;
    public final long[] timestamps;
    public final ScreenshotDataRecord[] data;

    public SessionScreenshotCacheData(
            final String sessionUUID, 
            final Collection<ScreenshotDataRecord> data) {
        
        this.sessionUUID = sessionUUID;
        this.data = data.toArray(new ScreenshotDataRecord[0]);
        Arrays.sort(
                this.data, 
                Comparator.comparing(ScreenshotDataRecord::getTimestamp));
        this.timestamps = new long[this.data.length];
        for (int i = 0; i < this.data.length; i++) {
            this.timestamps[i] = this.data[i].getTimestamp();
        }
    }
    
    public ScreenshotDataRecord getAt(Long timestamp) {
        if (timestamp == null) {
            return data[0];
        }
        
        final int i = Arrays.binarySearch(timestamps, timestamp);
        if (i >= 0) {
            return data[i];
        } else {
            int i1 = Math.abs(i) - 2;
            if (i1 < 0) {
                i1 = 0;
            } else if (i1 > data.length - 1) {
                i1 = data.length - 1;
            }
            return data[i1];
        }
    }

}
