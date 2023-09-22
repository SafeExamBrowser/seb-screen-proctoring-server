package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.utils.Result;

public interface GroupingService {

    Result<TimelineViewData> groupDataForTimeline(FilterMap filterMap);


}
