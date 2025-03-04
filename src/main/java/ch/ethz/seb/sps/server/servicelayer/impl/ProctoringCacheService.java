/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.utils.Result;

@Lazy
@Component
public class ProctoringCacheService {

    private static final Logger log = LoggerFactory.getLogger(ProctoringCacheService.class);

    /** Active group caches */
    public static final String ACTIVE_GROUP_CACHE = "ACTIVE_GROUP_CACHE";

    /** Active session token cache. Tokens per active group */
    public static final String SESSION_TOKENS_CACHE = "SESSION_TOKENS_CACHE";

    /** Active session cache. */
    public static final String ACTIVE_SESSION_CACHE = "ACTIVE_SESSION_CACHE";

    /** Total session count cache. */
    public static final String TOTAL_SESSION_COUNT = "TOTAL_SESSION_COUNT_CACHE";

    private final SessionDAO sessionDAO;
    private final GroupDAO groupDAO;

    public ProctoringCacheService(
            final SessionDAO sessionDAO,
            final GroupDAO groupDAO) {

        this.sessionDAO = sessionDAO;
        this.groupDAO = groupDAO;
    }

    @Cacheable(
            cacheNames = ACTIVE_GROUP_CACHE,
            key = "#groupUUID",
            unless = "#result == null")
    public Group getActiveGroup(final String groupUUID) {
        final Result<Group> groupResult = this.groupDAO.byModelId(groupUUID);
        if (groupResult.hasError()) {
            log.error("Failed to load active group: {}", groupUUID, groupResult.getError());
            return null;
        } else {
            final Group group = groupResult.get();
            if (group.terminationTime != null) {
                log.warn("Group: {} is not active anymore. Skip caching", group.name);
                return null;
            }
            return group;
        }
    }

    @CacheEvict(
            cacheNames = { ACTIVE_GROUP_CACHE, SESSION_TOKENS_CACHE, TOTAL_SESSION_COUNT },
            key = "#groupUUID")
    public void evictGroup(final String groupUUID) {
        if (log.isTraceEnabled()) {
            log.trace("Eviction of running group from cache: {}", groupUUID);
        }
    }

    @Cacheable(
            cacheNames = SESSION_TOKENS_CACHE,
            key = "#groupUUID",
            unless = "#result == null")
    public Collection<String> getLiveSessionTokens(final String groupUUID) {

        final Result<Collection<String>> liveSessions = this.sessionDAO
                .allLiveSessionUUIDsByGroupId(groupDAO.modelIdToPK(groupUUID));

        if (liveSessions.hasError()) {
            log.error("Failed to load live sessions for group: {}", groupUUID, liveSessions.getError());
            return null;
        } else {
            return liveSessions.get();
        }
    }

    @CacheEvict(
            cacheNames = { SESSION_TOKENS_CACHE, TOTAL_SESSION_COUNT },
            key = "#groupUUID")
    public void evictSessionTokens(final String groupUUID) {
        if (log.isTraceEnabled()) {
            log.trace("Eviction of group based session tokens from cache, group: {}", groupUUID);
        }
    }

    @Cacheable(
            cacheNames = ACTIVE_SESSION_CACHE,
            key = "#sessionUUID",
            unless = "#result == null")
    public Session getSession(final String sessionUUID) {
        final Result<Session> sessionByModelId = this.sessionDAO.byModelId(sessionUUID);
        if (sessionByModelId.hasError()) {
            log.error("Failed to load session by model id: {}", sessionUUID, sessionByModelId.getError());
            return null;
        }

        return sessionByModelId.get();
    }

    @CacheEvict(
            cacheNames = ACTIVE_SESSION_CACHE,
            key = "#sessionUUID")
    public void evictSession(final String sessionUUID) {
        if (log.isTraceEnabled()) {
            log.trace("Eviction of session from cache, sessionUUID: {}", sessionUUID);
        }
    }

    @Cacheable(
            cacheNames = TOTAL_SESSION_COUNT,
            key = "#groupUUID",
            unless = "#result == null")
    public Integer getTotalSessionCount(final String groupUUID, final Long groupId) {
        Result<Long> result = sessionDAO.allSessionCount(groupId);
        
        if (result.hasError()) {
            log.warn("Failed to get total session count for group: {} cause: {}", groupId, result.getError().toString());
        }
        
        return result.map(Long::intValue).getOr(null);
    }

}
