/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ch.ethz.seb.sps.server.ServiceConfig;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.servicelayer.AutomatedDeletionService;
import ch.ethz.seb.sps.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class AutomatedDeletionServiceImpl implements AutomatedDeletionService {

    private static final Logger log = LoggerFactory.getLogger(AutomatedDeletionServiceImpl.class);
    
    
    
    private final ExamDAO examDAO;
    private final TaskScheduler taskScheduler;
    
    private final Set<Long> toDelete = new HashSet<>();

    public AutomatedDeletionServiceImpl(
            final ExamDAO examDAO,
            @Qualifier(value = ServiceConfig.SYSTEM_SCHEDULER)  final TaskScheduler taskScheduler) {
        
        this.examDAO = examDAO;
        this.taskScheduler = taskScheduler;

    }
    
    @Override
    public void init() {
        // TODO at the moment we do not have auto deletion. Maybe later
        // triggered every hour...
//        this.taskScheduler.scheduleWithFixedDelay(
//                this::update,
//                java.time.Duration.ofMillis(Constants.HOUR_IN_MILLIS));
    }
    
    private void update() {
        try {
            
            if (log.isDebugEnabled()) {
                log.debug("Process Exam auto-delete check");
            }
            
            toDelete.addAll(examDAO.getAllForDeletion().getOr(Collections.emptyList()));
            
            if (!toDelete.isEmpty()) {
                processDeletion();
            }
            
        } catch (Exception e) {
            log.error("Failed to update automated Exam deletion: {}", e.getMessage());
        }
    }

    private void processDeletion() {
        try {

            new HashSet<>(toDelete).forEach(id -> {
                
                log.info("Automatically delete Exam: {}", id);
                
                examDAO
                    .delete(String.valueOf(id))
                    .onError(error -> log.error(
                            "Failed to delete Exam on automated deletion: {}",
                            error.getMessage()))
                    .onSuccess(key -> toDelete.remove(id));
            });
            
        } catch (Exception e) {
            log.error("Failed to delete Exam on automated deletion: {}", e.getMessage());
        }
    }
}
