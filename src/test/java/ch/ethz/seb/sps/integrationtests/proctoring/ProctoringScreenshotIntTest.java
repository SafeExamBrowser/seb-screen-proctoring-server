package ch.ethz.seb.sps.integrationtests.proctoring;

import static org.junit.Assert.*;

import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.server.weblayer.AdminProctorController;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ProctoringScreenshotIntTest extends ServiceTest_PROCTORING {

    @Autowired
    private AdminProctorController adminProctorController;

    @Test
    public void testbla(){

        //use case 1
        //1. click on a group in the running exams section
        // /group

        //2. go to the gallery view and get the live sessions
        // /group/uuid

        //3. watch the live proctoring
        // /screenshot-data/uuid
        // /search/timeline/uuid
        // /screenshot-timestamps/uuid


        //4. search for a specific metadata
        // /search/sessions/day
        // /search/sessions
        // /search/timeline

        //GIVEN





        //WHEN

        //THEN


        ScreenshotViewData test = this.adminProctorController.getScreenshotViewData("9cfb99c0-34a5-4ffd-a11c-4d9790b3f24c");

        System.out.println();

    }


}
