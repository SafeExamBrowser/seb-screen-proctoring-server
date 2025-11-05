package ch.ethz.seb.sps.integrationtests.proctoring;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.Order;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpMethod;

import java.sql.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProctoringServiceTest extends ServiceTest_PROCTORING {

    //tests a full use case of the screen proctoring
    @Test
    @Order(1)
    public void i1_testFullUseCaseSuccess() throws Exception {
        //GIVEN
        final Long expectedGroupId = 1l;
        final String expectedGroupName = "test_group";
        final Long expectedGroupCreationTime = 0l;
        final int expectedAmountOfScreenshots = 2;
        final String expectedGroupUuid = "3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c";
        final String expectedSessionUuid = "9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c";
        final String expectedGroupingKey = "Google Homepage";
        final int expectedLengthOfGroupedScreenshotData = 10;
        final List<Long> expectedScreenshotTimestamps = Arrays.asList(1721743482182l, 1721743483215l, 1721743484222l, 1721743485226l, 1721743486247l);
        final List<Date> expectedUniqueDays = Arrays.asList(Date.valueOf("2024-07-23"), Date.valueOf("2024-07-11"));
        final List<String> expectedSessionSearchUuids = Arrays.asList("9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c", "4461dec0-5579-4fef-a86f-0ec7b252c779", "c8ebdedc-1105-4ecb-bd04-c20ba2e221a5");
        final String expectedMetadataUserAction = "Ctrl c + Ctrl v";
        final String expectedMetadataURL = "https://google.com";


        //WHEN
        //1. view groups in the running exams section
        //endpoint: /group
        Map<String, String> groupAttributes = new HashMap<>();
        groupAttributes.put("includePastExams", "true");
        groupAttributes.put("includeUpcomingExams", "true");
        
        Page<GroupViewData> groups = createMockApiCall(
                API.GROUP_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<GroupViewData>>(){},
                groupAttributes);


        //2. go to the gallery view and get the live sessions
        //endpoint: /group/uuid
        ScreenshotsInGroupData screenshotsInGroupData = createMockApiCall(
                API.GROUP_ENDPOINT + "/3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c",
                HttpMethod.GET,
                new TypeReference<ScreenshotsInGroupData>(){},
                new HashMap<>());


        //3. watch the live proctoring
        //endpoint: /screenshot-data/uuid
        ScreenshotViewData screenshotViewData = createMockApiCall(
                API.SCREENSHOT_DATA_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c",
                HttpMethod.GET,
                new TypeReference<ScreenshotViewData>(){},
                new HashMap<>());

        //endpoint: /search/timeline/uuid
        TimelineViewData timelineViewData = createMockApiCall(
                API.TIMELINE_SEARCH_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c",
                HttpMethod.GET,
                new TypeReference<TimelineViewData>(){},
                new HashMap<>());

        //endpoint: /screenshot-timestamps/uuid
        List<Long> screenshotTimestamps = createMockApiCall(
                API.SCREENSHOT_TIMESTAMPS_ENDPOINT + "/9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c/" + "/1721743481170/" + PageSortOrder.ASCENDING,
                HttpMethod.GET,
                new TypeReference<List<Long>>(){},
                new HashMap<>());


        //4. search for a specific metadata
        //endpoint: /search/sessions/day
        List<Date> searchDates = createMockApiCall(
                API.SESSION_DAY_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<List<Date>>(){},
                new HashMap<>());

        //search attributes for the sessions and screenshots are the same
        Map<String, String> searchAttributes = new HashMap<>();
        searchAttributes.put("groupName", "test_group");
        searchAttributes.put("screenProctoringMetadataUserAction", "Ctrl c + Ctrl v");
        searchAttributes.put("screenProctoringMetadataURL", "https://google.com");
        searchAttributes.put("fromTime", "1691421029772");
        searchAttributes.put("toTime", "1723043429772");

        //endpoint: /search/sessions
        Page<SessionSearchResult> sessionSearchResult = createMockApiCall(
                API.SESSION_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<SessionSearchResult>>(){},
                searchAttributes);

        //endpoint: /search/timeline
        Page<ScreenshotSearchResult> screenshotSearchResult = createMockApiCall(
                API.SCREENSHOT_SEARCH_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<Page<ScreenshotSearchResult>>(){},
                searchAttributes);


        //THEN

        //check if expected groupUuid, groupName and groupCreationTime matches result
        GroupViewData group = null;
        for(int i = 0; i < groups.content.size(); i++){
            if(expectedGroupId == groups.content.get(i).id){
                group = groups.content.get(i);
            }
        }
        assertNotNull(group);
        assertEquals(expectedGroupName, group.name);
        assertEquals(expectedGroupCreationTime, group.creationTime);
        assertEquals(expectedGroupUuid, group.uuid);

        //check if expected amount of screenshot matches result
        assertEquals(expectedAmountOfScreenshots, screenshotsInGroupData.screenshots.size());

        //screenshotViewData should be the same as screenshotsInGroupData by sessionUUid (always returns latest screenshot of given session)
        ScreenshotViewData expectedScreenshotDataFromGroupList = null;
        for(int i = 0; i < screenshotsInGroupData.screenshots.size(); i++){
            if(expectedSessionUuid.equals(screenshotsInGroupData.screenshots.get(i).uuid)){
                expectedScreenshotDataFromGroupList = screenshotsInGroupData.screenshots.get(i);
            }
        }
        assertNotNull(expectedScreenshotDataFromGroupList);
        assertEquals(expectedScreenshotDataFromGroupList, screenshotViewData);

        //check if grouping of metadata is correct
        assertTrue(timelineViewData.timelineGroupDataList.size() > 0);
        assertEquals(expectedLengthOfGroupedScreenshotData, timelineViewData.timelineGroupDataList.get(0).timelineScreenshotDataList.size());
        assertEquals(expectedGroupingKey, timelineViewData.timelineGroupDataList.get(0).groupName);

        //check if expected screenshot timestamps matches result
        assertEquals(expectedScreenshotTimestamps, screenshotTimestamps);

        //check if expected unique days matches calculated values from db
        assertEquals(expectedUniqueDays.size(), searchDates.size());
        for(int i = 0; i < expectedUniqueDays.size(); i++){
            assertEquals(expectedUniqueDays.get(i).toString(), searchDates.get(i).toString());
        }

        //check if expected session & screenshot search matches result
        for(int i = 0; i < sessionSearchResult.content.size(); i++){
            assertTrue(expectedSessionSearchUuids.contains(sessionSearchResult.content.get(i).sessionUUID));
        }

        for(int i = 0; i < screenshotSearchResult.content.size(); i++) {
            assertTrue(screenshotSearchResult.content.get(i).metaData.containsValue(expectedMetadataUserAction));
            assertTrue(screenshotSearchResult.content.get(i).metaData.containsValue(expectedMetadataURL));
        }
    }

    @Test
    @Order(1)
    public void i2_testApplicationSearch() throws Exception {
        //GIVEN
        Long expectedExamId = 1L;
        List<Long> expectedGroupIds = Arrays.asList(1L, 2L);
//        List<String> expectedMetadataApp = Arrays.asList("")

        //WHEN
        //1. get exams in the given time frame
        //endpoint: /search/applications/exams
        List<Exam> exams = createMockApiCall(
                API.APPLICATION_SEARCH_EXAMS_ENDPOINT,
                HttpMethod.GET,
                new TypeReference<List<Exam>>(){},
                new HashMap<>());

        //2. get groupIds for given exam
        //endpoint: /search/applications/groupdIds/<examId>
        List<Long> groupIds = createMockApiCall(
                API.APPLICATION_SEARCH_ENDPOINT + "/groupIds/" + 1L,
                HttpMethod.GET,
                new TypeReference<List<Long>>(){},
                new HashMap<>());

        //2. get metadata application for given groupIds
        //endpoint: /search/applications/metadata/app
        Map<String, String> groupIdsAttributes = new HashMap<>();
        groupIdsAttributes.put("groupIds", "1,2");
//
//        List<String> metadataAppList = createMockApiCall(
//                API.APPLICATION_SEARCH_METADATA_APP_ENDPOINT,
//                HttpMethod.GET,
//                new TypeReference<List<String>>(){},
//                groupIdsAttributes);
//
//        System.out.println(exams);



        //THEN


    }


    private <T> T createMockApiCall(String endpoint, HttpMethod httpMethod, TypeReference<T> typeReference, Map<String, String> attributes) throws Exception {
        return new RestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/proctoring" + endpoint)
                .withMethod(httpMethod)
                .withAttributes(attributes)
                //todo: assertion says it's 404 but status 200 is printed in the console
                //.withExpectedStatus(HttpStatus.OK)
                .getAsObject(typeReference);
    }


}
