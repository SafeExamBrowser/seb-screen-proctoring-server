/*
 * Copyright (c) 2024 ETH Zürich, IT Services
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

import ch.ethz.seb.sps.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SessionServiceHealthControlImpl.class);

    private final ThreadPoolExecutor uploadExecutor;
    private final ThreadPoolExecutor downloadExecutor;
    private final ScreenshotStoreService screenshotStoreService;
    private final DataSource dataSource;
    private final DataSourceHealthIndicator dataSourceHealthIndicator;
    
    private long debug_log_time_interval = 1000;
    private long debug_log_last_log_time = 0;
    
    private int simulateHealthIssue = -1;

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
    public void setSimulateHealthIssue(int simulateHealthIssue) {
        this.simulateHealthIssue = simulateHealthIssue;
        if (this.simulateHealthIssue > 10) {
            this.simulateHealthIssue = 10;
        }
        if (this.simulateHealthIssue < 1) {
            this.simulateHealthIssue = -1;
        }
    }

    @Override
    public int getUploadHealthIndicator() {
        return mapPoolSizeToIndicatorRange(this.uploadExecutor.getActiveCount());
    }

    @Override
    public int getDownloadHealthIndicator() {
        return mapPoolSizeToIndicatorRange(this.downloadExecutor.getActiveCount());
    }

    @Override
    public int getStoreHealthIndicator() {
        return this.screenshotStoreService.getStoreHealthIndicator();
    }

    @Override
    public int getDataSourceHealthIndicator() {
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
        if (simulateHealthIssue > 0) {
            return simulateHealthIssue;
        }
        
        final int uploadHealthIndicator = getUploadHealthIndicator();
        final int downloadHealthIndicator = getDownloadHealthIndicator();
        final int storeHealthIndicator = getStoreHealthIndicator();
        final int dataSourceHealthIndicator = getDataSourceHealthIndicator();
        
        if (log.isDebugEnabled()) {
            long now = Utils.getMillisecondsNow();
            if (now - debug_log_last_log_time > debug_log_time_interval) {
                debug_log_last_log_time = now;
                if (uploadHealthIndicator > 0) {
                    log.info("uploadHealthIndicator: {}", uploadHealthIndicator);
                }
                if (downloadHealthIndicator > 0) {
                    log.info("downloadHealthIndicator: {}", downloadHealthIndicator);
                }
                if (storeHealthIndicator > 0) {
                    log.info("storeHealthIndicator: {}", storeHealthIndicator);
                }
                if (dataSourceHealthIndicator > 0) {
                    log.info("dataSourceHealthIndicator: {}", dataSourceHealthIndicator);
                }
            }
        }

        return Math.max(
                Math.max(uploadHealthIndicator, downloadHealthIndicator),
                Math.max(storeHealthIndicator, dataSourceHealthIndicator));
    }

    // map size between THREAD_POOL_SIZE_INDICATOR_MAP_MIN ... THREAD_POOL_SIZE_INDICATOR_MAP_MAX to 0 ... HEALTH_INDICATOR_MAX
    private int mapPoolSizeToIndicatorRange(final int size) {
        if (size > THREAD_POOL_SIZE_INDICATOR_MAP_MAX) {
            return HEALTH_INDICATOR_MAX;
        }

        final int offset = size - THREAD_POOL_SIZE_INDICATOR_MAP_MIN;
        if (offset < 0) {
            return 0;
        }
        return (int) (offset / (float) THREAD_POOL_SIZE_INDICATOR_MAP_MAX * HEALTH_INDICATOR_MAX);
    }

}
