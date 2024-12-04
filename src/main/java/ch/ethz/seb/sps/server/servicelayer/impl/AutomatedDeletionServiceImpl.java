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
import ch.ethz.seb.sps.utils.Utils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class AutomatedDeletionServiceImpl implements AutomatedDeletionService {

    private static final Logger log = LoggerFactory.getLogger(AutomatedDeletionServiceImpl.class);
    
    
    
    private final ExamDAO examDAO;
    private final TaskScheduler taskScheduler;
    private final int APPLY_DELETION_AFTER_HOUR_OF_DAY;
    private final int APPLY_DELETION_BEFORE_HOUR_OF_DAY;
    
    private final Set<Long> toDelete = new HashSet<>();

    public AutomatedDeletionServiceImpl(
            final ExamDAO examDAO,
            @Qualifier(value = ServiceConfig.SYSTEM_SCHEDULER)  final TaskScheduler taskScheduler,
            @Value("${sps.data.autodelete.only.after.hour.utc:0}") final int APPLY_DELETION_AFTER_HOUR_OF_DAY,
            @Value("${sps.data.autodelete.only.after.hour.utc:6}") final int APPLY_DELETION_BEFORE_HOUR_OF_DAY) {
        
        this.examDAO = examDAO;
        this.taskScheduler = taskScheduler;
        this.APPLY_DELETION_AFTER_HOUR_OF_DAY = APPLY_DELETION_AFTER_HOUR_OF_DAY;
        this.APPLY_DELETION_BEFORE_HOUR_OF_DAY = APPLY_DELETION_BEFORE_HOUR_OF_DAY;
    }
    
    @Override
    public void init() {
        // triggered every hour...
        this.taskScheduler.scheduleWithFixedDelay(
                this::update,
                java.time.Duration.ofMillis(Constants.HOUR_IN_MILLIS));
    }
    
    private void update() {
        try {
            
            if (log.isDebugEnabled()) {
                log.debug("Process Exam auto-delete check");
            }
            
            toDelete.addAll(examDAO.getAllForDeletion().getOr(Collections.emptyList()));
            
            if (!toDelete.isEmpty()) {
                DateTime now = Utils.toDateTimeUTC(Utils.getMillisecondsNow());
                
                // processed only in the morning hours (UTC)
                if (now.hourOfDay().get() < APPLY_DELETION_BEFORE_HOUR_OF_DAY) {
                    processDeletion();
                } 
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
