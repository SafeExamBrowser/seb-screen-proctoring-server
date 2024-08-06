package ch.ethz.seb.sps.integrationtests.proctoring;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.domain.model.service.TimelineViewData;
import ch.ethz.seb.sps.domain.model.user.ServerUser;
import ch.ethz.seb.sps.server.datalayer.dao.impl.UserDAOBatis;
import ch.ethz.seb.sps.server.weblayer.AdminProctorController;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.sql.Date;
import java.util.List;

import static ch.ethz.seb.sps.integrationtests.proctoring.utils.ServiceTest_PROCTORING_constants.PAGE_NUMBER;
import static ch.ethz.seb.sps.integrationtests.proctoring.utils.ServiceTest_PROCTORING_constants.PAGE_SIZE;
import static ch.ethz.seb.sps.integrationtests.proctoring.utils.ServiceTest_PROCTORING_constants.SORT;

public class ProctoringServiceTest extends ServiceTest_PROCTORING {

    @Autowired
    private AdminProctorController adminProctorController;


    @Autowired
    private UserDAOBatis userDAOBatis;

    //tests a full use case of the screen proctoring
    @Test
    public void testFullUseCaseSuccess() throws Exception {
        //GIVEN

        ServerUser user = userDAOBatis.byUsername("super-admin").getOrThrow();

        System.out.println();



        //WHEN
        //1. view groups in the running exams section
//        Page<GroupViewData> groups = this.adminProctorController.getGroups(PAGE_NUMBER, PAGE_SIZE, SORT, false, false, new Htt()); //endpoint: /group

        //endpoint: /group
//        Page<GroupViewData> groups = new RestAPITestHelper()
//                .withAccessToken(getSebAdminAccess())
//                .withPath(API.GROUP_ENDPOINT)
//                .withMethod(HttpMethod.GET)
//                .withExpectedStatus(HttpStatus.OK)
//                .getAsObject(new TypeReference<>() {
//                });

        System.out.println();





        //2. go to the gallery view and get the live sessions
//        ScreenshotsInGroupData screenshotsInGroupData = this.adminProctorController.getSessionsByGroup(); //endpoint: /group/uuid
//
//        //3. watch the live proctoring
//        ScreenshotViewData screenshotViewData = this.adminProctorController.getScreenshotViewData(); //endpoint: /screenshot-data/uuid
//        TimelineViewData timelineViewData = this.adminProctorController.getTimelineViewData(); //endpoint: /search/timeline/uuid
//        List<Long> screenshotTimestamps = this.adminProctorController.getScreenshotTimestamps(); //endpoint: /screenshot-timestamps/uuid
//
//        //4. search for a specific metadata
//        List<Date> searchDates = this.adminProctorController.getMatchingDaysForSessionSearch(); //endpoint: /search/sessions/day
//        Page<SessionSearchResult> sessionSearchResult = this.adminProctorController.searchSessions(); //endpoint: /search/sessions
//        Page<ScreenshotSearchResult> screenshotSearchResult = this.adminProctorController.searchScreenshots(); //endpoint: /search/timeline

        //THEN





    }


}
