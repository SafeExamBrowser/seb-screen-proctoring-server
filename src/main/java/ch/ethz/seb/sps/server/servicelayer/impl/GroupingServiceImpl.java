package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.TimelineGroupData;
import ch.ethz.seb.sps.domain.model.service.TimelineScreenshotData;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.servicelayer.GroupingService;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import ch.ethz.seb.sps.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class GroupingServiceImpl implements GroupingService {

    private static final Logger log = LoggerFactory.getLogger(GroupingServiceImpl.class);

    private final ScreenshotDataDAO screenshotDataDAO;
    private final ProctoringService proctoringService;


    public GroupingServiceImpl(
            final ScreenshotDataDAO screenshotDataDAO,
            final ProctoringService proctoringService) {

        this.screenshotDataDAO = screenshotDataDAO;
        this.proctoringService = proctoringService;
    }



    @Override
    public Result<TimelineViewData> groupDataForTimeline(FilterMap filterMap) {

        return Result.tryCatch(() -> {

            TimelineViewData timelineViewData = new TimelineViewData(
                    filterMap.getString(API.PARAM_SESSION_ID),
                    new ArrayList<>()
            );

            List<ScreenshotSearchResult> screenshotSearchResultList = createScreenshotSearchResultList(filterMap);
            if(screenshotSearchResultList.size() == 0){
                return timelineViewData;
            }

            List<TimelineGroupData> groups = createTimelineGroupDataList(screenshotSearchResultList);
            timelineViewData.setTimelineGroupDataList(groups);

            return timelineViewData;
        });
    }


    private List<ScreenshotSearchResult> createScreenshotSearchResultList(FilterMap filterMap){
        Result<Collection<ScreenshotSearchResult>> screenshotSearchResult = this.screenshotDataDAO.searchScreenshotData(filterMap)
                .map(this.proctoringService::createScreenshotSearchResult);

        return screenshotSearchResult.get().stream().toList();
    }

    private List<TimelineGroupData> createTimelineGroupDataList(List<ScreenshotSearchResult> screenshotSearchResultList){
        List<TimelineGroupData> groups = new ArrayList<>();

        int groupOrder = 0;
        ScreenshotSearchResult firstScreenshot = screenshotSearchResultList.get(0);
        TimelineGroupData currentGroup = createTimelineGroupData(
                groupOrder,
                firstScreenshot.getMetaData().get(API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE),
                firstScreenshot
        );


        for(int i = 1; i < screenshotSearchResultList.size(); i++) {
            ScreenshotSearchResult currentScreenshot = screenshotSearchResultList.get(i);
            String metadataWindowTitle = currentScreenshot.getMetaData().get(API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE);

            if(currentGroup.getGroupName() != null && currentGroup.getGroupName().equals(metadataWindowTitle)){
                currentGroup.addItemToScreenshotData(
                        new TimelineScreenshotData(
                                currentScreenshot.getTimestamp(),
                                currentScreenshot.getMetaData()
                        ));

            }else{
                groups.add(currentGroup);
                groupOrder++;

                currentGroup = createTimelineGroupData(
                        groupOrder,
                        metadataWindowTitle,
                        currentScreenshot
                );
            }
        }

        if(!groups.contains(currentGroup)){
            groups.add(currentGroup);
        }

        return groups;
    }

    private TimelineGroupData createTimelineGroupData(
            int groupOrder,
            String groupName,
            ScreenshotSearchResult screenshot)
    {

        return new TimelineGroupData(
                groupOrder,
                groupName,
                new ArrayList<>(
                        List.of(
                                new TimelineScreenshotData(
                                        screenshot.timestamp,
                                        screenshot.metaData
                                )
                        )
                )
        );
    }
}
