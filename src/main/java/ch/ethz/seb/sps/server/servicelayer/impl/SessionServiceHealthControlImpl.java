/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;
import ch.ethz.seb.sps.server.servicelayer.ScreenshotStoreService;
import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;

@Service
public class SessionServiceHealthControlImpl implements SessionServiceHealthControl {

    private final ThreadPoolExecutor uploadExecutor;
    private final ThreadPoolExecutor downloadExecutor;
    private final ScreenshotStoreService screenshotStoreService;
    private final DataSource dataSource;
    private final DataSourceHealthIndicator dataSourceHealthIndicator;

    public SessionServiceHealthControlImpl(
            @Qualifier(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR) final Executor upload,
            @Qualifier(value = ServiceConfig.SCREENSHOT_DOWNLOAD_API_EXECUTOR) final Executor download,
            final ScreenshotStoreService screenshotStoreService,
            final DataSource dataSource) {

        this.uploadExecutor = ((ThreadPoolTaskExecutor) upload).getThreadPoolExecutor();
        this.downloadExecutor = ((ThreadPoolTaskExecutor) download).getThreadPoolExecutor();
        this.screenshotStoreService = screenshotStoreService;
        this.dataSource = dataSource;
        this.dataSourceHealthIndicator = new DataSourceHealthIndicator(dataSource);

    }

    @EventListener(ServiceInitEvent.class)
    private void init() {
        ServiceInit.INIT_LOGGER.info("----> SessionServiceHealthControl initialized");
    }

    @Override
    public int getUploadHealthIndicator() {
        // 0 ... 10
        return this.uploadExecutor.getPoolSize() / 1000 * this.uploadExecutor.getActiveCount();
    }

    @Override
    public int getDownloadHealthIndicator() {
        // 0 ... 10
        return this.downloadExecutor.getPoolSize() / 1000 * this.downloadExecutor.getActiveCount();
    }

    @Override
    public int getStoreHealthIndicator() {
        return this.screenshotStoreService.getStoreHealthIndicator();
    }

    @Override
    public int getDataSourceHelathIndicator() {
        if (this.dataSource instanceof HikariDataSource) {
            final HikariDataSource hds = (HikariDataSource) this.dataSource;
            final HikariPoolMXBean hikariPoolMXBean = hds.getHikariPoolMXBean();
            final int idleConnections = hikariPoolMXBean.getIdleConnections();
            final int activeConnections = hikariPoolMXBean.getActiveConnections();
        }
        final Health health = this.dataSourceHealthIndicator.getHealth(true);
        final Map<String, Object> details = health.getDetails();
        // TODO check what is in details
        final Status status = health.getStatus();
        // TODO
        return 0;
    }

    @Override
    public int getOverallLoadIndicator() {
        final int uploadHealthIndicator = getUploadHealthIndicator();
        final int downloadHealthIndicator = getDownloadHealthIndicator();
        final int storeHealthIndicator = getStoreHealthIndicator();
        final int dataSourceHelathIndicator = getDataSourceHelathIndicator();

        return (uploadHealthIndicator + downloadHealthIndicator + storeHealthIndicator + dataSourceHelathIndicator) / 4;
    }

}
