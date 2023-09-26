package ch.ethz.seb.sps.server.servicelayer.impl;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.TimelineGroupData;
import ch.ethz.seb.sps.domain.model.service.TimelineScreenshotData;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.impl.ScreenshotDataDAOBatis;
import ch.ethz.seb.sps.server.servicelayer.ProctoringService;
import ch.ethz.seb.sps.utils.Result;
import com.google.gson.Gson;
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
public class GroupingServiceImplTest {

    private static final Logger log = LoggerFactory.getLogger(GroupingServiceImplTest.class);
    
    private static String SESSION_UUID = "SESSION_UUID";
    private static Long TIMESTAMP = 123456789L;
    private static String GROUP_NAME1 = "Moodle Page 1";
    private static String GROUP_NAME2 = "Moodle Page 2";
    private static String METADATA_KEY = "screenProctoringMetadataWindowTitle";
    private static String METADATA_WINDOW_TITLE_VALUE1 = "Moodle Page 1";
    private static String METADATA_WINDOW_TITLE_VALUE2 = "Moodle Page 2";


    @Mock
    private ScreenshotDataDAOBatis screenshotDataDAO;

    @Mock
    private ProctoringService proctoringService;

    @InjectMocks
    private GroupingServiceImpl groupingService;

    @Test
    public void testGroupDataForTimelineWithoutMetadata(){
        //GIVEN
        FilterMap filterMap = filterMapWithOnlySessionUUID();
        Result<TimelineViewData> expectedTimelineViewData = genericTimelineViewData();

        //WHEN
        when(this.screenshotDataDAO.searchScreenshotData(filterMap))
                .thenReturn(createScreenshotDataRecord());
        when(this.proctoringService.createScreenshotSearchResult(createScreenshotDataRecord().get()))
                .thenReturn(createScreenshotSearchResultCollection());

        Result<TimelineViewData> timelineViewData = this.groupingService.groupDataForTimeline(filterMap);

        //print results as json
//        log.info("expected result:");
//        log.info(new Gson().toJson(expectedTimelineViewData.get()));
//        log.info("----------------");
//
//        log.info("actual result:");
//        log.info(new Gson().toJson(timelineViewData.get()));
//        log.info("----------------");

        //THEN
        assertEquals(new Gson().toJson(expectedTimelineViewData.get()), new Gson().toJson(timelineViewData.get()));
    }


    private FilterMap filterMapWithOnlySessionUUID(){
        FilterMap filterMap = new FilterMap();
        filterMap.putIfAbsent(API.PARAM_SESSION_ID, SESSION_UUID);

        return filterMap;
    }
    
    private HashMap<String, String> metadataHashMap(String metadataValue){
        return new HashMap<String, String>() {
            {
                put(METADATA_KEY, metadataValue);
            }
        };
    }

    //-------expected result---------//
    private Result<TimelineViewData> genericTimelineViewData(){
        return Result.tryCatch(() -> {
            return new TimelineViewData(
                    SESSION_UUID,
                    createGroupDataList()
            );
        });
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
                                metadataHashMap(metadataValue)
                        ),
                        new TimelineScreenshotData(
                                TIMESTAMP,
                                metadataHashMap(metadataValue)
                        )
                )
        );

    }
    //-------------------------------//

    //-------mock data from db---------//
    private Result<Collection<ScreenshotDataRecord>> createScreenshotDataRecord(){
        return Result.tryCatch(() -> {
            return new ArrayList<>(
                    List.of(
                            new ScreenshotDataRecord(
                                    1L,
                                    SESSION_UUID,
                                    TIMESTAMP,
                                    1,
                                    ""
                            )
                    )
            );
        });
    }


    private Collection<ScreenshotSearchResult> createScreenshotSearchResultCollection(){
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
                metadataHashMap(metadataValue)
        );
    }
    //-------------------------------//

}