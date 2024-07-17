package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.ExamViewData;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.server.ServiceInfo;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringCacheService;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProctoringServiceTest {

    private final JSONMapper jsonMapper = new JSONMapper();

    //generic data
    private static final Long GENERIC_LONG = 1L;
    private static final String EMPTY_STRING = "";
    private static final Long ID = 777L;

    //group data
    private static final Long GROUP_ID = 999L;
    private static final String GROUP_UUID = "e7555417-382c-4200-99bb-7f80023cfeaf";

    private static final String UUID1 = "be481b6a-bf1e-490e-9d18-c97355b01bfa";
    private static final String UUID2 = "be481b6a-bf1e-490e-9d18-c97355b01bfb";
    private static final String UUID3 = "be481b6a-bf1e-490e-9d18-c97355b01bfc";
    private static final String UUID4 = "be481b6a-bf1e-490e-9d18-c97355b01bfd";
    private static final String UUID5 = "be481b6a-bf1e-490e-9d18-c97355b01bfe";
    private static final String UUID6 = "be481b6a-bf1e-490e-9d18-c97355b01bff";
    private static final List<String> UUID_LIST = new ArrayList<>(Arrays.asList(UUID1, UUID2, UUID3, UUID4, UUID5, UUID6));

    private static final String GROUP_NAME = "test group";
    private static final String GROUP_DESCRIPTION = "test description";

    //screenshot metadata
    private static final String CLIENT_NAME = "seb_0d03539e-699d-48a3-8335-853800c5a1ff_3";
    private static final String CLIENT_IP = "127.0.0.1";
    private static final String CLIENT_MACHINE_NAME = "3.7.0 BETA (x64)";
    private static final String CLIENT_OS_NAME = "Windows 10, Microsoft Windows NT 10.0.19045.0 (x64)";
    private static final String CLIENT_VERSION = "3.7.0 BETA (x64)";
    private static final String IMAGE_LINK = "http://127.0.0.1:8090/admin-api/v1/proctoring/screenshot/" + UUID1 + "/954";
    private static final Session.ImageFormat IMAGE_FORMAT_PNG = Session.ImageFormat.PNG;

    //session data
    private static final int NUMBER_OF_LIVE_SESSIONS = 5;
    private static final int NUMBER_OF_LIVE_SESSIONS_EMPTY = 0;
    private static final int NUMBER_OF_SESSIONS = 5;
    private static final int NUMBER_OF_SESSIONS_EMPTY = 0;

    //time data
    private static final Long TIMESTAMP = 1712137768194L;
    private static final Long CREATION_TIME = 1712137768194L;
    private static final Long LAST_UPDATE_TIME = 1712137768194L;
    private static final Long TERMINATION_TIME = 1712220293956L;

    //paging data
    private static final int PAGE_NUMBER = 1;
    private static final int PAGE_SIZE = 9;
    private static final int PAGE_SIZE_EMPTY = 0;
    private static final String SORT_BY = "";
    private static final PageSortOrder SORT_ORDER = PageSortOrder.ASCENDING;

    //exam data

    //remaining data
    private static final FilterMap FILTER_MAP = new FilterMap();


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

    @InjectMocks
    private ProctoringServiceImpl proctoringService;


    @Test
    public void getSessionsByGroupWithEmptyResult() throws JsonProcessingException {
        //GIVEN
        ScreenshotsInGroupData excpectedScreenshotsInGroupData = createGroupWithEmptyList();

        when(this.serviceInfo.isDistributed())
                .thenReturn(false);

        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createGenericGroup());

        when(this.proctoringCacheService.getLiveSessionTokens(any()))
                .thenReturn(new ArrayList<>());

        when(this.sessionDAO.allSessionCount(any()))
                .thenReturn(Result.of(0L));

        when(this.screenshotDataDAO.allLatestIn(any()))
                .thenReturn(Result.of(new HashMap<>()));

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

    private ScreenshotsInGroupData createGroupWithEmptyList(){
        return new ScreenshotsInGroupData(
                GROUP_UUID,
                EMPTY_STRING,
                EMPTY_STRING,
                NUMBER_OF_LIVE_SESSIONS_EMPTY,
                NUMBER_OF_SESSIONS_EMPTY,
                PAGE_NUMBER,
                PAGE_SIZE_EMPTY,
                SORT_BY,
                SORT_ORDER,
                new ArrayList<>(),
                new ExamViewData(null, null, null, null, null)
        );
    }

    private Group createGenericGroup(){
        return new Group(
                GENERIC_LONG,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                GENERIC_LONG,
                GENERIC_LONG,
                GENERIC_LONG,
                null,
                null
        );
    }

    private Session createGenericSession(boolean isActive){
        return new Session(
                GENERIC_LONG,
                GENERIC_LONG,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                EMPTY_STRING,
                IMAGE_FORMAT_PNG,
                GENERIC_LONG,
                GENERIC_LONG,
                isActive ? null : GENERIC_LONG
        );
    }

    private List<String> createLiveSessionTokenList(){
        List<String> liveSessionTokens = new ArrayList<>();
        for(int i = 0; i < NUMBER_OF_LIVE_SESSIONS_EMPTY; i++){
            liveSessionTokens.add(UUID1);
        }

        return liveSessionTokens;
    }

    private Map<String, ScreenshotDataRecord> createScreenshotDataRecordMap(){
        Map<String, ScreenshotDataRecord> screenshotDataRecordMap = new HashMap<>();

        for(int i = 0; i < NUMBER_OF_LIVE_SESSIONS_EMPTY; i++){
            screenshotDataRecordMap.put(UUID_LIST.get(i), createScreenshotDataRecord(UUID_LIST.get(i)));
        }

        return screenshotDataRecordMap;
    }

    private ScreenshotDataRecord createScreenshotDataRecord(final String sessionUuid){
        return new ScreenshotDataRecord(
                ID,
                sessionUuid,
                TIMESTAMP,
                IMAGE_FORMAT_PNG.code,
                EMPTY_STRING
        );
    }
}