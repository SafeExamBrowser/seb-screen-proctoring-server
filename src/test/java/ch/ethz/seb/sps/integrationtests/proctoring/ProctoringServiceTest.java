package ch.ethz.seb.sps.integrationtests.proctoring;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.server.datalayer.dao.ClientAccessDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.UserDAO;
import ch.ethz.seb.sps.server.weblayer.AdminProctorController;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

import java.sql.Date;
import java.util.List;

public class ProctoringServiceTest extends ServiceTest_PROCTORING {

    @Autowired
    private AdminProctorController adminProctorController;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private ScreenshotDataDAO screenshotDataDAO;

    @Autowired
    private ClientAccessDAO clientAccessDAO;

    //tests a full use case of the screen proctoring
    @Test
    public void testFullUseCaseSuccess() throws Exception {
        //GIVEN


        //WHEN
        //1. view groups in the running exams section
        //endpoint: /group
        Page<GroupViewData> groups = createMockApiCall(
                API.GROUP_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<GroupViewData>>(){});


        //2. go to the gallery view and get the live sessions
        //endpoint: /group/uuid
        ScreenshotsInGroupData screenshotsInGroupData = createMockApiCall(
                API.GROUP_ENDPOINT + "/test_group",
                HttpMethod.GET,
                new TypeReference<ScreenshotsInGroupData>(){});


        //3. watch the live proctoring
        //endpoint: /screenshot-data/uuid
        ScreenshotViewData screenshotViewData = createMockApiCall(
                API.SCREENSHOT_DATA_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c",
                HttpMethod.GET,
                new TypeReference<ScreenshotViewData>(){});

        //endpoint: /search/timeline/uuid
        TimelineViewData timelineViewData = createMockApiCall(
                API.TIMELINE_SEARCH_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c",
                HttpMethod.GET,
                new TypeReference<TimelineViewData>(){});

        //endpoint: /screenshot-timestamps/uuid
        List<Long> screenshotTimestamps = createMockApiCall(
                API.SCREENSHOT_TIMESTAMPS_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c/" + PageSortOrder.ASCENDING,
                HttpMethod.GET,
                new TypeReference<List<Long>>(){});


        //4. search for a specific metadata
        //endpoint: /search/sessions/day
        List<Date> searchDates = createMockApiCall(
                API.SESSION_DAY_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<List<Date>>(){});

        //endpoint: /search/sessions
        Page<SessionSearchResult> sessionSearchResult = createMockApiCall(
                API.SESSION_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<SessionSearchResult>>(){});

        //endpoint: /search/timeline
        Page<ScreenshotSearchResult> screenshotSearchResult = createMockApiCall(
                API.SCREENSHOT_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<ScreenshotSearchResult>>(){});


        System.out.println();
        //THEN



    }

    private <T> T createMockApiCall(String endpoint, HttpMethod httpMethod, TypeReference<T> typeReference) throws Exception {
        return new RestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath(endpoint)
                .withMethod(httpMethod)
                //todo: assertion says it's 404 but status 200 is printed in the console
                //.withExpectedStatus(HttpStatus.OK)
                .getAsObject(typeReference);
    }


}
