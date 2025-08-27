/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import static ch.ethz.seb.sps.server.ServiceConfig.SYSTEM_SCHEDULER;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Lazy
@Component
public class ServiceUpdateTask implements DisposableBean {

    private final ServiceInfo serviceInfo;
    private final long updateInterval;

    public ServiceUpdateTask(
            final ServiceInfo serviceInfo,
            @Value("${sps.webservice.distributed.update:15000}") final long updateInterval) {

        this.serviceInfo = serviceInfo;
        this.updateInterval = updateInterval;
    }

    @EventListener(ServiceInitEvent.class)
    private void init() {
        ServiceInit.INIT_LOGGER.info("------> Activate background update task");
        ServiceInit.INIT_LOGGER.info("------> Task runs on an update interval of {}", this.updateInterval);

        this.serviceInfo.updateMaster();

        if (this.serviceInfo.isMaster()) {
            ServiceInit.INIT_LOGGER.info("-------->");
            ServiceInit.INIT_LOGGER.info("--------> This instance has become master!");
            ServiceInit.INIT_LOGGER.info("-------->");
        }

    }

    @Scheduled(
            fixedDelayString = "${sps.webservice.distributed.update:15000}",
            initialDelay = 5000,
            scheduler = SYSTEM_SCHEDULER)
    private void sessionUpdateTask() {
        this.serviceInfo.updateMaster();
    }

    @Override
    public void destroy() {
        ServiceInit.INIT_LOGGER.info("-----> Should down SPS Server...");
    }

}
