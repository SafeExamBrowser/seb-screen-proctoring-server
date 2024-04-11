package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.TimelineGroupData;
import ch.ethz.seb.sps.domain.model.service.TimelineScreenshotData;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.impl.ScreenshotDataDAOBatis;
import ch.ethz.seb.sps.server.servicelayer.impl.GroupingServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GroupingServiceTest {

    private static final Logger log = LoggerFactory.getLogger(GroupingServiceTest.class);
    private final JSONMapper jsonMapper = new JSONMapper();

    private static final String SESSION_UUID = "SESSION_UUID";
    private static final Long TIMESTAMP = 123456789L;
    private static final String GROUP_NAME1 = "Moodle Page 1";
    private static final String GROUP_NAME2 = "Moodle Page 2";
    private static final String METADATA_KEY = "screenProctoringMetadataWindowTitle";
    private static final String METADATA_WINDOW_TITLE_VALUE1 = "Moodle Page 1";
    private static final String METADATA_WINDOW_TITLE_VALUE2 = "Moodle Page 2";


    @Mock
    private ScreenshotDataDAO screenshotDataDAO;

    @Mock
    private ProctoringService proctoringService;

    @InjectMocks
    private GroupingServiceImpl groupingService;

    @Test
    public void groupDataForTimelineWithoutMetadata() throws JsonProcessingException {
        //GIVEN
        FilterMap filterMap = createFilterMapWithOnlySessionUUID();
        Result<TimelineViewData> expectedTimelineViewData = createGenericTimelineViewData();

        when(this.screenshotDataDAO.searchScreenshotData(filterMap))
                .thenReturn(screenshotDataRecord());
        when(this.proctoringService.createScreenshotSearchResult(screenshotDataRecord().get()))
                .thenReturn(screenshotSearchResultCollection());

        //WHEN
        Result<TimelineViewData> timelineViewData = this.groupingService.groupDataForTimeline(filterMap);

        //THEN
        assertEquals(this.jsonMapper.writeValueAsString(expectedTimelineViewData.get()), this.jsonMapper.writeValueAsString(timelineViewData.get()));
    }

    private FilterMap createFilterMapWithOnlySessionUUID(){
        FilterMap filterMap = new FilterMap();
        filterMap.putIfAbsent(API.PARAM_SESSION_ID, SESSION_UUID);

        return filterMap;
    }

    private HashMap<String, String> createMetadataHashMap(String metadataValue){
        return new HashMap<String, String>() {
            {
                put(METADATA_KEY, metadataValue);
            }
        };
    }

    //-------expected result---------//
    private Result<TimelineViewData> createGenericTimelineViewData() {
        return Result.of(
                new TimelineViewData(
                        SESSION_UUID,
                        createGroupDataList()
                )
        );
    }

    private List<TimelineGroupData> createGroupDataList(){
        return new ArrayList<>(
                List.of(
                        new TimelineGroupData(
                                0,
                                GROUP_NAME1,
                                createScreenshotDataListGroup1()
                        ),
                        new TimelineGroupData(
                                1,
                                GROUP_NAME2,
                                createScreenshotDataListGroup2()
                        ),
                        new TimelineGroupData(
                                2,
                                GROUP_NAME1,
                                createScreenshotDataListGroup1()
                        ),
                        new TimelineGroupData(
                                3,
                                GROUP_NAME2,
                                createScreenshotDataListGroup2()
                        )
                )
        );
    }

    private List<TimelineScreenshotData> createScreenshotDataListGroup1(){
        return createScreenshotDataList(METADATA_WINDOW_TITLE_VALUE1);

    }

    private List<TimelineScreenshotData> createScreenshotDataListGroup2(){
        return createScreenshotDataList(METADATA_WINDOW_TITLE_VALUE2);
    }

    private List<TimelineScreenshotData> createScreenshotDataList(String metadataValue){
        return new ArrayList<>(
                List.of(
                        new TimelineScreenshotData(
                                TIMESTAMP,
                                createMetadataHashMap(metadataValue)
                        ),
                        new TimelineScreenshotData(
                                TIMESTAMP,
                                createMetadataHashMap(metadataValue)
                        )
                )
        );

    }
    //-------------------------------//

    //-------mock data from db---------//
    private Result<Collection<ScreenshotDataRecord>>  screenshotDataRecord() {
        return Result.of(
                new ArrayList<>(
                        List.of(
                                new ScreenshotDataRecord(
                                        1L,
                                        SESSION_UUID,
                                        TIMESTAMP,
                                        1,
                                        ""
                                )
                        )
                )
        );
    }


    private Collection<ScreenshotSearchResult> screenshotSearchResultCollection(){
        return new ArrayList<>(
                List.of(
                        createScreenshotSearchResult(GROUP_NAME1, METADATA_WINDOW_TITLE_VALUE1),
                        createScreenshotSearchResult(GROUP_NAME2, METADATA_WINDOW_TITLE_VALUE1),
                        createScreenshotSearchResult(GROUP_NAME1, METADATA_WINDOW_TITLE_VALUE2),
                        createScreenshotSearchResult(GROUP_NAME2, METADATA_WINDOW_TITLE_VALUE2),
                        createScreenshotSearchResult(GROUP_NAME1, METADATA_WINDOW_TITLE_VALUE1),
                        createScreenshotSearchResult(GROUP_NAME2, METADATA_WINDOW_TITLE_VALUE1),
                        createScreenshotSearchResult(GROUP_NAME1, METADATA_WINDOW_TITLE_VALUE2),
                        createScreenshotSearchResult(GROUP_NAME2, METADATA_WINDOW_TITLE_VALUE2)
                )
        );
    }

    private ScreenshotSearchResult createScreenshotSearchResult(String groupName, String metadataValue){
        return new ScreenshotSearchResult(
                "",
                groupName,
                1L,
                "",
                1L,
                1L,
                "",
                "",
                "",
                "",
                "",
                1L,
                TIMESTAMP,
                Session.ImageFormat.PNG,
                createMetadataHashMap(metadataValue)
        );
    }
}