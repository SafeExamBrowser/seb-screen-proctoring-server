/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.io.InputStream;
import java.util.List;

import ch.ethz.seb.sps.utils.Result;

public interface ScreenshotDAO {

    /** Get an Image via InputStream.
     * 
     * @param pk The PK of the image to get
     * @param sessionUUID The session UUID where the image belongs to.
     * @return Result refer to an InputSteam for the image data */
    Result<InputStream> getImage(Long pk, String sessionUUID);

    /** Deletes all images for a specified session
     * 
     * @param sessionUUID The session UUID
     * @param screenShotPKs List of all image PKs to delete.
     * @return Result refer to the list of image PK that has been deleted or to an error when happened */
    Result<List<Long>> deleteAllForSession(String sessionUUID, List<Long> screenShotPKs);

}