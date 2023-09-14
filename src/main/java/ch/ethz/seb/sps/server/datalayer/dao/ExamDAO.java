package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.utils.Result;

import java.util.Collection;

public interface ExamDAO extends ActivatableEntityDAO<Exam, Exam> {

    boolean existsByUUID(String examUUID);

    Result<Exam> createNew(String name);

    Result<Collection<Exam>> pksByExamName(final FilterMap filterMap);


}
