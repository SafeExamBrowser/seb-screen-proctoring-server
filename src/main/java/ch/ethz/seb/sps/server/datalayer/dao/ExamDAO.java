package ch.ethz.seb.sps.server.datalayer.dao;

import java.util.Collection;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.utils.Result;

public interface ExamDAO extends ActivatableEntityDAO<Exam, Exam> {

    boolean existsByUUID(String examUUID);

    Result<Collection<Exam>> pksByExamName(final FilterMap filterMap);

    boolean isExamRunning(Long examId);

    Result<Collection<Exam>> getExamsStarted(final FilterMap filterMap);
    
    Result<Collection<Long>> getAllForDeletion();

    boolean hasRunningLifeExams();
}
