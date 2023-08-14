/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.domain.model.service.ScreenshotData;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDAO;
import ch.ethz.seb.sps.utils.Result;

@Service
@ConditionalOnExpression("'${sps.data.store.adapter}'.equals('FILESYS_RDBMS')")
public class ScreenshotDAOFile implements ScreenshotDAO {

    private final String rootDir;

    public ScreenshotDAOFile(@Value("${sps.data.store.file.dir:/screenshots/}") final String rootDir) {
        this.rootDir = rootDir;
    }

    @EventListener(ServiceInitEvent.class)
    public void init() {
        ServiceInit.INIT_LOGGER.info("----> Screenshot Store FILESYS_RDBMS: initialized");
    }

    @Override
    public Result<InputStream> getImage(
            final Long pk,
            final String sessionUUID) {

        return Result.tryCatch(() -> {

            final String dir = this.rootDir + sessionUUID + "/";
            final String fileName = ScreenshotData.SCREEN_PREFIX + pk;

            final FileSystemResource fileResource = new FileSystemResource(dir + fileName);
            return fileResource.getInputStream();
        });
    }

    @Override
    public Result<Long> storeImage(
            final Long pk,
            final String sessionUUID,
            final InputStream in) {

        return Result.tryCatch(() -> {

            final String dir = this.rootDir + sessionUUID + "/";
            final String fileName = ScreenshotData.SCREEN_PREFIX + pk;

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

    @Override
    public Result<List<Long>> deleteAllForSession(final String sessionUUID, final List<Long> screenShotPKs) {
        return Result.tryCatch(() -> {
            final String dir = this.rootDir + screenShotPKs;

            final FileSystemResource fileSystemResource = new FileSystemResource(dir);
            if (!fileSystemResource.exists()) {
                throw new IllegalArgumentException("Session screenshot directory: " + dir + " not found!");
            }

            if (!fileSystemResource.getFile().isDirectory()) {
                throw new IllegalArgumentException("Session screenshot path: " + dir + " is not a directory!");
            }

            FileUtils.deleteDirectory(fileSystemResource.getFile());

            return screenShotPKs;
        });
    }

}
