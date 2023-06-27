/*
 * Copyright (c) 2023 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.server.servicelayer.SessionServiceHealthControl;

@Lazy
@Component
public class ServiceUpdateTask implements DisposableBean {

    private final ServiceInfo serviceInfo;
    private final SessionServiceHealthControl sessionServiceHealthControl;
    private final long updateInteval;

    public ServiceUpdateTask(
            final ServiceInfo serviceInfo,
            final SessionServiceHealthControl sessionServiceHealthControl,
            @Value("${sps.webservice.distributed.update:15000}") final long updateInteval) {

        this.serviceInfo = serviceInfo;
        this.sessionServiceHealthControl = sessionServiceHealthControl;
        this.updateInteval = updateInteval;
    }

    @EventListener(ServiceInitEvent.class)
    private void init() {
        ServiceInit.INIT_LOGGER.info("------> Activate background update task");
        ServiceInit.INIT_LOGGER.info("------> Task runs on an update interval of {}", this.updateInteval);

        this.serviceInfo.updateMaster();

        if (this.serviceInfo.isMaster()) {
            ServiceInit.INIT_LOGGER.info("-------->");
            ServiceInit.INIT_LOGGER.info("--------> This instance has become master!");
            ServiceInit.INIT_LOGGER.info("-------->");
        }

    }

    @Scheduled(
            fixedDelayString = "${sps.webservice.distributed.update:15000}",
            initialDelay = 5000)
    private void examSessionUpdateTask() {

        this.serviceInfo.updateMaster();

        ServiceInit.INIT_LOGGER.info("--------> Service Health: {}",
                this.sessionServiceHealthControl.getOverallLoadIndicator());

    }

    @Override
    public void destroy() throws Exception {
        // TODO Auto-generated method stub

    }

}
