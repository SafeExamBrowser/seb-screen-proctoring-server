/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.server.datalayer.dao;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.utils.Result;

public interface SessionDAO extends ActivatableEntityDAO<Session, Session> {

    /** Creates a new Session for a specified group
     * 
     * @param groupUUID The group UUID
     * @param uuid The Session UUID that is used to identify the new session
     * @param userSessionName The username for the session
     * @param clientIP The client IP of the new session
     * @param clientMachineName The client machine name for the new session
     * @param clientOSName The client OS name for the new session
     * @param clientVersion The client version of the new session
     * @param imageFormat The image format of the new session
     * @return Result refer to the new created session or to an error when happened*/
    Result<Session> createNew(
            String groupUUID,
            String uuid,
            String userSessionName,
            String clientIP,
            String clientMachineName,
            String clientOSName,
            String clientVersion,
            ImageFormat imageFormat);

    /** Get a collection of all session uuid that are actually active for a given group.
     * 
     * @param groupId The group id (PK)
     * @return Result refer to the resulting collection or to an error if happened */
    Result<Collection<String>> allLiveSessionUUIDsByGroupId(Long groupId);

    /** Get a collection of all session uuid for a given group.
     * 
     * @param groupId The group id (PK)
     * @return Result refer to the resulting collection or to an error if happened */
    Result<Collection<String>> allSessionUUIDsByGroupId(Long groupId);

    /** Get a count of all active live session of a given group.
     * 
     * @param groupId The group id (PK)
     * @return Result refer to the count or to an error when happened */
    Result<Long> allLiveSessionCount(Long groupId);

    /** Get a count of all session of a given group.
     * 
     * @param groupId The group id (PK)
     * @return Result refer to the count or to an error when happened */
    Result<Long> allSessionCount(Long groupId);

    /** Close a given session.
     * 
     * @param sessionUUID The session UUID to close.
     * @return Result refer to the UUID of the closed session or to an error when happened */
    Result<String> closeSession(String sessionUUID);

    /** Close all open sessions within a given group.
     * 
     * @param groupPK The group id (PK)
     * @return Result refer to a collection of EntityKey of all sessions that has been closed, or to an error when happened*/
    Result<Collection<EntityKey>> closeAllSessionsForGroup(Long groupPK);

    /** Deletes all Session for a given list of groups
     * 
     * @param groupPKs List of group PKs to delete all sessions for
     * @return Result refer to a collection of EntityKey of all deleted session, or to an error when happened*/
    Result<Collection<EntityKey>> deleteAllForGroups(final List<Long> groupPKs);

    /** Get the number of screenshots for a given session filtered by screenshot metadata.
     * Filter criteria are of type API.ScreenshotMetadataType and are AND combined.
     * 
     * @param uuid The session UUID
     * @param filterMap filter map containing filter criteria to apply to before count
     * @return the number of filtered screenshots for a given session */
    Long getNumberOfScreenshots(String uuid, FilterMap filterMap);

    /** Indicates if there are any session data for a given list of groups.
     * 
     * @param groupIds Collection of group ids (PK) to check for.
     * @return Result refer to the indicator or to an error when happened.*/
    Result<Boolean> hasAnySessionData(Collection<Long> groupIds);

    /** Get a list of dates for which screenshot data exists for a given filter criteria.
     * This is used in multiple step searches where first a list of dates is presented where screenshot data exists
     * and only if one is interested in screenshot data of a specific date, the search is applied to get the data for
     * only this date, to improve performance.
     * <p> 
     * Filter criteria:
     * <pre>
     *     active = filterMap.getBooleanObject(API.ACTIVE_FILTER);
     *     fromTime = filterMap.getLong(API.PARAM_FROM_TIME);
     *     toTime = filterMap.getLong(API.PARAM_TO_TIME);
     *     groupPKs = filterMap.getString(Domain.SESSION.ATTR_GROUP_ID);
     *     sessionUUID = filterMap.contains(API.PARAM_SESSION_ID)
     *  </pre>
     * @param filterMap The map containing the filter criteria
     * @return Result refer to a list of dates where screenshots exists or to an error when happened.*/
    Result<List<Date>> queryMatchingDaysForSessionSearch(final FilterMap filterMap);

    /** This is only for distributed setups where we can get all session UUIDs of a given group that
     * are out of sync on its cached instances. This checks the update_time timestamps of cached instances with the once
     * on the database and gives all Session UUID that has different update_time and where updated by another SPS service.
     * @param groupId The group id (PK)
     * @param updateTimes List of update_time timestamps of all cached sessions.
     * @return Result refer to the list of session UUIDs that needs update, ot to an error when happened*/
    Result<List<String>> allTokensThatNeedsUpdate(Long groupId, Set<Long> updateTimes);

    /** Get the encryption key of the session used to encrypt/decrypt client side stored screenshot data.
     * 
     * @param uuid The UUID of the session
     * @return Result refer to the encryption key of the session or to an error when happened */
    Result<String> getEncryptionKey(String uuid);

    /** This us used to close a given open session at a given time.
     * 
     * @param sessionUUID The UUID of the session to close at given time
     * @param termination_time The timestamp on which to close the session (unix timestamp im milliseconds)
     * @return Result refer to the close timestamp or to an error when happened.*/
    Result<Long> closeAt(String sessionUUID, Long termination_time);

    /** Reduces the given list of sessionUUIDs to a list of session UUIDs of all closed sessions include in the given set
     * 
     * @param sessionUUIDs the session UUIDs
     * @return a list of session UUIDs of all closed sessions include in the given set*/
    Result<List<String>> getAllClosedSessionsIn(Set<String> sessionUUIDs);
}