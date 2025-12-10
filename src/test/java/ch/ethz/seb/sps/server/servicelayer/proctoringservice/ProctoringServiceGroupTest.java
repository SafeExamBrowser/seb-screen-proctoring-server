package ch.ethz.seb.sps.server.servicelayer.proctoringservice;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.LiveProctoringCacheService;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringCacheService;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.FILTER_MAP_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.GROUP_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.IMAGE_LINK;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.NUMBER_OF_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.PAGE_NUMBER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.PAGE_SIZE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.PAGE_SIZE_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.SORT_BY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstantsTest.SORT_ORDER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createExam;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createGenericGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createScreenshotGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createScreenshotGroupWithEmptyList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createLiveSessionTokenList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createRealisticGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createScreenshotDataRecordMap;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceUtilsTest.createSession;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProctoringServiceGroupTest {

    private final JSONMapper jsonMapper = new JSONMapper();


    @Mock
    private ServiceInfo serviceInfo;

    //@InjectMocks
    @Mock
    private ProctoringCacheService proctoringCacheService;
    
    @Mock
    private LiveProctoringCacheService liveProctoringCacheService;

    @Mock
    private SessionDAO sessionDAO;
    
    @Mock
    private GroupDAO groupDAO;

    @Mock
    private ScreenshotDataDAO screenshotDataDAO;

    @Mock
    private ExamDAO examDAO;

    @Mock
    private JSONMapper jsonMapperMocked;

    @InjectMocks
    private ProctoringServiceImpl proctoringService;



//    @BeforeEach
//    public void setUp() {
////        when(this.serviceInfo.isDistributed())
////                .thenReturn(false);
//   
//    }

    @Test
    public void testGetSessionsByGroup_EmptyResult() throws JsonProcessingException {
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
                FILTER_MAP_EMPTY);

        //THEN
        assertFalse(screenshotsInGroupDataEmpty.hasError());
        assertEquals(this.jsonMapper.writeValueAsString(excpectedScreenshotsInGroupData), this.jsonMapper.writeValueAsString(screenshotsInGroupDataEmpty.getOrThrow()));
    }


    @Test
    public void testGetSessionsByGroup() throws JsonProcessingException {
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
                FILTER_MAP_EMPTY);

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

    private void mockDependenciesForEmptyResult(){

        
        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createGenericGroup());

        when(this.proctoringCacheService.getLiveSessionTokens(any()))
                .thenReturn(new ArrayList<>());

        when(this.screenshotDataDAO.allOfMappedToSession(any()))
                .thenReturn(Result.of(new HashMap<>()));
    }

    private void mockDependenciesForRealisticResult() throws JsonProcessingException {
        when( this.liveProctoringCacheService.getLatestSSDataId(any()))
                .thenReturn(-1L);
        
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
        when(this.screenshotDataDAO.allOfMappedToSession(any()))
                .thenReturn(Result.of(screenshotDataRecordMap));

        when(this.examDAO.byModelId(any()))
                .thenReturn(Result.of(createExam()));
        
        when(this.proctoringCacheService.getTotalSessionCount(any(), any()))
                .thenReturn(NUMBER_OF_SESSIONS);
    }

}