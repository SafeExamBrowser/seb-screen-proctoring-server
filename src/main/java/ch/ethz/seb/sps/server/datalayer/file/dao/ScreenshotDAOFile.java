/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.file.dao;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.servicelayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ScreenshotDAOFile implements ScreenshotDAO {

    private final String rootDir = "/screenshots/";

    @Override
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionId) {

        return Result.tryCatch(() -> {

            final String dir = this.rootDir + sessionId + "/";
            final String fileName = "screen" + pk;

            final FileSystemResource fileResource = new FileSystemResource(dir + fileName);
            return fileResource.getInputStream();
        });
    }

    @Override
    public Result<Long> storeImage(
            final Long pk,
            final String sessionId,
            final InputStream in) {

        return Result.tryCatch(() -> {

            final String dir = this.rootDir + sessionId + "/";
            final String fileName = "screen" + pk;

            final FileSystemResource fileSystemResource = new FileSystemResource(dir);
            if (!fileSystemResource.exists()) {
                fileSystemResource.getFile().mkdirs();
            }

            final FileSystemResource fileResource = (FileSystemResource) fileSystemResource.createRelative(fileName);
            if (!fileResource.exists()) {
                fileResource.getFile().createNewFile();
            }

            final OutputStream outputStream = fileResource.getOutputStream();

            IOUtils.copy(in, outputStream);

            outputStream.close();

            return pk;
        });
    }

}
