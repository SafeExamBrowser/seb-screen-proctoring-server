package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;
import java.util.Set;

import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.utils.Result;

public interface ExamDAO extends ActivatableEntityDAO<Exam, Exam> {

    boolean existsByUUID(String examUUID);

    Result<Collection<Exam>> pksByExamName(FilterMap filterMap);

    boolean isExamRunning(Long examId);

    Result<Collection<Exam>> getExamsWithin(FilterMap filterMap, Collection<Long> prePredicated);

    /** Gets all exams that startTime is older than a given deleteDueTime in UTC
     *
     * @param deleteDueTimeUTC the given due time
     * @return Gets all exams that startTime is older than a given deleteDueTime */
    Result<Collection<Exam>> getExamsForScheduledDeletion(Long deleteDueTimeUTC);

    boolean hasRunningLifeExams();

    /** Get all Exam ids of all Exams that has the given institution id.
     * @param instId institution id if null an {@link IllegalArgumentException is thrown}
     * @return list of all Exams within the given institution */
    Collection<Long> getAllExamIdsOfInstitution(Long instId);

    /** This only updates the institution and the supporter data of a specified Exam.
     *  Checks first if there is a change and if so logs and applies the update.
     * @param examUUID the Exam uuid to update
     * @param institutionId the new institution id to update
     * @param supporter Collection of supporter ids to update
     * @return true when an update happened, false if not */
    boolean updateInstitution(String examUUID, Long institutionId, Collection<String> supporter);
}

