/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import ch.ethz.seb.sps.domain.api.JSONMapper;

@Configuration
@EnableAsync
public class ServiceConfig {

    /** Spring bean name of user password encoder */
    public static final String USER_PASSWORD_ENCODER_BEAN_NAME = "userPasswordEncoder";
    /** Spring bean name of client (application) password encoder */
    public static final String CLIENT_PASSWORD_ENCODER_BEAN_NAME = "clientPasswordEncoder";

    public static final String SCREENSHOT_UPLOAD_API_EXECUTOR = "SCREENSHOT_UPLOAD_API_EXECUTOR";
    public static final String SCREENSHOT_DOWNLOAD_API_EXECUTOR = "SCREENSHOT_DOWNLOAD_API_EXECUTOR";

    @Lazy
    @Bean
    public JSONMapper jsonMapper() {
        return new JSONMapper();
    }

    /** Password encoder used for user passwords (stronger protection) */
    @Bean(USER_PASSWORD_ENCODER_BEAN_NAME)
    public PasswordEncoder userPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    /** Password encode used for client (application) passwords */
    @Bean(CLIENT_PASSWORD_ENCODER_BEAN_NAME)
    public PasswordEncoder clientPasswordEncoder() {
        return new BCryptPasswordEncoder(4);
    }

    @Bean(name = SCREENSHOT_UPLOAD_API_EXECUTOR)
    public Executor screenhortUploadThreadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(200);
        executor.setMaxPoolSize(2000);
        executor.setQueueCapacity(200);
        executor.setThreadPriority(Thread.MAX_PRIORITY);
        executor.setThreadNamePrefix("upload-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }

    @Bean(name = SCREENSHOT_DOWNLOAD_API_EXECUTOR)
    public Executor screenhortDownloadThreadPoolTaskExecutor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(100);
        executor.setQueueCapacity(500);
        executor.setThreadPriority(Thread.NORM_PRIORITY);
        executor.setThreadNamePrefix("download-");
        executor.initialize();
        executor.setWaitForTasksToCompleteOnShutdown(false);
        return executor;
    }

    @Bean
    public TaskScheduler taskScheduler() {
        final ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(2);
        scheduler.setThreadNamePrefix("scheduled-task-");
        scheduler.setDaemon(true);

        return scheduler;
    }

}
