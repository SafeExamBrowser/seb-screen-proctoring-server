package ch.ethz.seb.sps.server.servicelayer.proctoringservice;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringCacheService;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.FILTER_MAP;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GROUP_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.IMAGE_LINK;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.NUMBER_OF_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_NUMBER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_SIZE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_SIZE_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.SORT_BY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.SORT_ORDER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createExam;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createGenericGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createScreenshotGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createScreenshotGroupWithEmptyList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createLiveSessionTokenList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createRealisticGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createScreenshotDataRecordMap;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringSeriveTestsUtils.createSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProctoringServiceGroupTest {

    private final JSONMapper jsonMapper = new JSONMapper();


    @Mock
    private ServiceInfo serviceInfo;

    @Mock
    private ProctoringCacheService proctoringCacheService;

    @Mock
    private SessionDAO sessionDAO;

    @Mock
    private ScreenshotDataDAO screenshotDataDAO;

    @Mock
    private ExamDAO examDAO;

    @Mock
    private JSONMapper jsonMapperMocked;

    @InjectMocks
    private ProctoringServiceImpl proctoringService;


    @Test
    public void getSessionsByGroupWithEmptyResult() throws JsonProcessingException {
        //GIVEN
        ScreenshotsInGroupData excpectedScreenshotsInGroupData = createScreenshotGroupWithEmptyList();
        mockDependenciesForEmptyResult();

        //WHEN
        Result<ScreenshotsInGroupData> screenshotsInGroupDataEmpty = this.proctoringService.getSessionsByGroup(
                GROUP_UUID,
                PAGE_NUMBER,
                PAGE_SIZE_EMPTY,
                SORT_BY,
                SORT_ORDER,
                FILTER_MAP);

        //THEN
        assertFalse(screenshotsInGroupDataEmpty.hasError());
        assertEquals(this.jsonMapper.writeValueAsString(excpectedScreenshotsInGroupData), this.jsonMapper.writeValueAsString(screenshotsInGroupDataEmpty.getOrThrow()));
    }


    @Test
    public void getSessionsByGroup() throws JsonProcessingException {
        //GIVEN
        ScreenshotsInGroupData excpectedScreenshotsInGroupData = createScreenshotGroup();

        mockDependenciesForRealisticResult();

        //WHEN
        Result<ScreenshotsInGroupData> screenshotsInGroupData = this.proctoringService.getSessionsByGroup(
                GROUP_UUID,
                PAGE_NUMBER,
                PAGE_SIZE,
                SORT_BY,
                SORT_ORDER,
                FILTER_MAP);

        //THEN
        assertFalse(screenshotsInGroupData.hasError());

        //compare singles values as metadata json as string is in different form
        assertEquals(excpectedScreenshotsInGroupData.groupUUID, screenshotsInGroupData.getOrThrow().groupUUID);
        assertEquals(excpectedScreenshotsInGroupData.groupDescription, screenshotsInGroupData.getOrThrow().groupDescription);
        assertEquals(excpectedScreenshotsInGroupData.groupName, screenshotsInGroupData.getOrThrow().groupName);
        assertEquals(excpectedScreenshotsInGroupData.numberOfSessions, screenshotsInGroupData.getOrThrow().numberOfSessions);
        assertEquals(excpectedScreenshotsInGroupData.numberOfLiveSessions, screenshotsInGroupData.getOrThrow().numberOfLiveSessions);
        assertEquals(excpectedScreenshotsInGroupData.screenshots.size(), screenshotsInGroupData.getOrThrow().screenshots.size());

        //compare metadata
        for(int i = 0; i < excpectedScreenshotsInGroupData.screenshots.size(); i++){
            Map<String, String> excpectedMetadata = excpectedScreenshotsInGroupData.screenshots.get(i).metaData;
            Map<String, String> metadata = screenshotsInGroupData.getOrThrow().screenshots.get(i).metaData;

            assertEquals(this.jsonMapper.writeValueAsString(excpectedMetadata), metadata.get("data"));
        }
    }

    @BeforeEach
    public void setUp() {
        when(this.serviceInfo.isDistributed())
                .thenReturn(false);
    }

    private void mockDependenciesForEmptyResult(){
        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createGenericGroup());

        when(this.proctoringCacheService.getLiveSessionTokens(any()))
                .thenReturn(new ArrayList<>());

        when(this.sessionDAO.allSessionCount(any()))
                .thenReturn(Result.of(0L));

        when(this.screenshotDataDAO.allLatestIn(any()))
                .thenReturn(Result.of(new HashMap<>()));
    }

    private void mockDependenciesForRealisticResult() throws JsonProcessingException {
        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createRealisticGroup());

        when(this.serviceInfo.getScreenshotRequestURI())
                .thenReturn(IMAGE_LINK);

        List<String> liveSessionTokens = createLiveSessionTokenList();
        when(this.proctoringCacheService.getLiveSessionTokens(any()))
                .thenReturn(liveSessionTokens);

        for(int i = 0; i < liveSessionTokens.size(); i++){
            when(this.proctoringCacheService.getSession(any()))
                    .thenReturn(createSession(liveSessionTokens.get(i)));
        }

        Map<String, ScreenshotDataRecord> screenshotDataRecordMap = createScreenshotDataRecordMap();
        when(this.screenshotDataDAO.allLatestIn(any()))
                .thenReturn(Result.of(screenshotDataRecordMap));

//        for(ScreenshotDataRecord record : screenshotDataRecordMap.values()){
//            when(this.jsonMapperMocked.readValue(record.getMetaData(), new TypeReference<Map<String, String>>() {}))
//                    .thenReturn(jsonMapper.readValue(record.getMetaData(), new TypeReference<Map<String, String>>() {}));
//        }

        when(this.examDAO.byModelId(any()))
                .thenReturn(Result.of(createExam()));

        when(this.sessionDAO.allSessionCount(any()))
                .thenReturn(Result.of((long)NUMBER_OF_SESSIONS));
    }

}