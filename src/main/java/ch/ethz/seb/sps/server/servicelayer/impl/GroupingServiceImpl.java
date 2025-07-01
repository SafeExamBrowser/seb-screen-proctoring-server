package ch.ethz.seb.sps.server.servicelayer.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

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

@Service
public class GroupingServiceImpl implements GroupingService {

    private final ScreenshotDataDAO screenshotDataDAO;
    private final ProctoringService proctoringService;

    public GroupingServiceImpl(
            final ScreenshotDataDAO screenshotDataDAO,
            final ProctoringService proctoringService) {

        this.screenshotDataDAO = screenshotDataDAO;
        this.proctoringService = proctoringService;
    }

    @Override
    public Result<TimelineViewData> groupDataForTimeline(final FilterMap filterMap) {

        return Result.tryCatch(() -> {

            final TimelineViewData timelineViewData = new TimelineViewData(
                    filterMap.getString(API.PARAM_SESSION_ID),
                    new ArrayList<>());

            final List<ScreenshotSearchResult> screenshotSearchResultList = createScreenshotSearchResultList(filterMap);
            if (screenshotSearchResultList.isEmpty()) {
                return timelineViewData;
            }

            final List<TimelineGroupData> groups = createTimelineGroupDataList(screenshotSearchResultList);
            timelineViewData.setTimelineGroupDataList(groups);

            return timelineViewData;
        });
    }

    private List<ScreenshotSearchResult> createScreenshotSearchResultList(final FilterMap filterMap) {
        final Result<Collection<ScreenshotSearchResult>> screenshotSearchResult =
                this.screenshotDataDAO.searchScreenshotData(filterMap)
                        .map(this.proctoringService::createScreenshotSearchResult);

        return screenshotSearchResult.get().stream().toList();
    }

    private List<TimelineGroupData> createTimelineGroupDataList(
            final List<ScreenshotSearchResult> screenshotSearchResultList) {
        final List<TimelineGroupData> groups = new ArrayList<>();

        int groupOrder = 0;
        final ScreenshotSearchResult firstScreenshot = screenshotSearchResultList.get(0);
        TimelineGroupData currentGroup = createTimelineGroupData(
                groupOrder,
                firstScreenshot.getMetaData().get(API.SCREENSHOT_META_DATA_APPLICATION),

                firstScreenshot);

        for (int i = 1; i < screenshotSearchResultList.size(); i++) {
            final ScreenshotSearchResult currentScreenshot = screenshotSearchResultList.get(i);
            final String metadataApplication =
            currentScreenshot.getMetaData().get(API.SCREENSHOT_META_DATA_APPLICATION);

            if (currentGroup.getGroupName() != null && currentGroup.getGroupName().equals(metadataApplication)) {
                currentGroup.addItemToScreenshotData(
                        new TimelineScreenshotData(
                                currentScreenshot.getTimestamp(),
                                currentScreenshot.getMetaData()));

            } else {
                groups.add(currentGroup);
                groupOrder++;

                currentGroup = createTimelineGroupData(
                        groupOrder,
                        metadataApplication,
                        currentScreenshot);
            }
        }

        if (!groups.contains(currentGroup)) {
            groups.add(currentGroup);
        }

        return groups;
    }

    private TimelineGroupData createTimelineGroupData(
            final int groupOrder,
            final String groupName,
            final ScreenshotSearchResult screenshot) {

        return new TimelineGroupData(
                groupOrder,
                groupName,
                new ArrayList<>(
                        List.of(
                                new TimelineScreenshotData(
                                        screenshot.timestamp,
                                        screenshot.metaData))));
    }
}
