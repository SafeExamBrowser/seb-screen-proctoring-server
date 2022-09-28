/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.ServiceInit;
import ch.ethz.seb.sps.server.ServiceInitEvent;

@Service
public class SessionServiceHealthControl {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceHealthControl.class);

    private final BlockingQueue<Runnable> queue;

    public SessionServiceHealthControl(
            @Qualifier(value = ServiceConfig.SCREENSHOT_UPLOAD_API_EXECUTOR) final Executor executor) {

        this.queue = ((ThreadPoolTaskExecutor) executor).getThreadPoolExecutor().getQueue();
    }

    @EventListener(ServiceInitEvent.class)
    private void init() {
        ServiceInit.INIT_LOGGER.info("----> ");
        ServiceInit.INIT_LOGGER.info("----> SessionServiceHealthControl initialized");
        ServiceInit.INIT_LOGGER.info("----> ");
    }

    public int getProcssingQueueLoad() {
        return this.queue.size();
    }

    @Scheduled(
            fixedDelay = 5000,
            initialDelay = 5000)
    public void checkQueueLoad() {
        log.debug("Session processing queue load: {}", getProcssingQueueLoad());
    }

}
