/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.ethz.seb.sps.domain.api.JSONMapper;

@Configuration
@EnableAsync
@EnableScheduling
public class ServiceConfig {
    
    public static final String SCREENSHOT_UPLOAD_API_EXECUTOR = "SCREENSHOT_UPLOAD_API_EXECUTOR";
    public static final String SCREENSHOT_DOWNLOAD_API_EXECUTOR = "SCREENSHOT_DOWNLOAD_API_EXECUTOR";
    public static final String SCREENSHOT_STORE_API_EXECUTOR = "SCREENSHOT_STORE_API_EXECUTOR";
    public static final String SYSTEM_SCHEDULER = "SYSTEM_SCHEDULER";

    @Lazy
    @Bean
    public JSONMapper jsonMapper() {
        return new JSONMapper();
    }

    /** Password encoder used for user passwords (stronger protection) */
    @Bean
    public PasswordEncoder userPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean(name = SCREENSHOT_UPLOAD_API_EXECUTOR)
    public Executor screenhortUploadThreadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(100);
        executor.setMaxPoolSize(400);
        executor.setQueueCapacity(0);
        executor.setThreadPriority(Thread.MAX_PRIORITY);
        executor.setThreadNamePrefix("upload-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean(name = SCREENSHOT_DOWNLOAD_API_EXECUTOR)
    public Executor screenhortDownloadThreadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(0);
        executor.setThreadPriority(Thread.NORM_PRIORITY);
        executor.setThreadNamePrefix("download-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(true);
        return executor;
    }

    @Bean(name = SCREENSHOT_STORE_API_EXECUTOR)
    public TaskScheduler batchStoreScreenScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("store-");
        scheduler.setThreadPriority(Thread.NORM_PRIORITY);
        scheduler.setDaemon(true);

        return scheduler;
    }

    @Bean(name = SYSTEM_SCHEDULER)
    public TaskScheduler systemScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(4);
        scheduler.setThreadNamePrefix("system-");
        scheduler.setThreadPriority(Thread.NORM_PRIORITY);
        scheduler.setDaemon(true);

        return scheduler;
    }

}
