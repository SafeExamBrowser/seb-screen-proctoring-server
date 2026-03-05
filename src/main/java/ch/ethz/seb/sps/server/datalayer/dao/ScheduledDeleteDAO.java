package ch.ethz.seb.sps.server.datalayer.dao;

import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import ch.ethz.seb.sps.utils.Nullable;
import ch.ethz.seb.sps.utils.Result;

import java.util.Collection;

public interface ScheduledDeleteDAO extends EntityDAO<ScheduledDelete, ScheduledDelete> {

    Result<ScheduledDelete> addInfo(Long scheduledDeleteId, Collection<ScheduledDeleteInfo> info);

    Result<Nullable<ScheduledDelete>> getDeleteReadyForProcessing();

    void startProcessing(Long deleteId);
    void endProcessing(Long deleteId);

    void startSingleDeletion(Long infoId);
    void endSingleDeletion(Long infoId, String errorInfo);


}
