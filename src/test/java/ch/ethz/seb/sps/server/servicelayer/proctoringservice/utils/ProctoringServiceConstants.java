package ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Session;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProctoringServiceConstants {

    //generic data
    public static final Long GENERIC_LONG = 1L;
    public static final String EMPTY_STRING = "";
    public static final Long ID = 777L;


    //time data
    public static final Long TIMESTAMP = 1712137768194L;
    public static final Long CREATION_TIME = 1712137768194L;
    public static final Long LAST_UPDATE_TIME = 1712137768194L;
    public static final Long TERMINATION_TIME = 1712220293956L;
    public static final Long START_TIME = 1712137768195L;
    public static final Long END_TIME = 1712137768196L;

    public static final Date DAY1 = Date.valueOf("2024-07-23");
    public static final Date DAY2 = Date.valueOf("2024-07-11");
    public static final Date DAY3 = Date.valueOf("2024-07-10");
    public static final Date DAY4 = Date.valueOf("2024-05-23");
    public static final Date DAY5 = Date.valueOf("2024-05-12");
    public static final Date DAY6 = Date.valueOf("2024-04-03");
    public static final Date DAY7 = Date.valueOf("2024-03-12");
    public static final Date DAY8 = Date.valueOf("2024-02-12");
    public static final Date DAY9 = Date.valueOf("2023-06-01");


    //group data
    public static final Long GROUP_ID = 999L;
    public static final String GROUP_UUID = "e7555417-382c-4200-99bb-7f80023cfeaf";

    public static final String UUID1 = "be481b6a-bf1e-490e-9d18-c97355b01bfa";
    public static final String UUID2 = "be481b6a-bf1e-490e-9d18-c97355b01bfb";
    public static final String UUID3 = "be481b6a-bf1e-490e-9d18-c97355b01bfc";
    public static final String UUID4 = "be481b6a-bf1e-490e-9d18-c97355b01bfd";
    public static final String UUID5 = "be481b6a-bf1e-490e-9d18-c97355b01bfe";
    public static final String UUID6 = "be481b6a-bf1e-490e-9d18-c97355b01bff";
    public static final List<String> UUID_LIST = new ArrayList<>(Arrays.asList(UUID1, UUID2, UUID3, UUID4, UUID5, UUID6));

    public static final String GROUP_NAME = "test group";
    public static final String GROUP_DESCRIPTION = "test group description";


    //screenshot data
    public static final long IMAGE_ID = 1l;

    public static final String CLIENT_NAME = "seb_0d03539e-699d-48a3-8335-853800c5a1ff_3";
    public static final String CLIENT_IP = "127.0.0.1";
    public static final String CLIENT_MACHINE_NAME = "3.7.0 BETA (x64)";
    public static final String CLIENT_OS_NAME = "Windows 10, Microsoft Windows NT 10.0.19045.0 (x64)";
    public static final String CLIENT_VERSION = "3.7.0 BETA (x64)";
    public static final String IMAGE_LINK = "http://127.0.0.1:8090/admin-api/v1/proctoring/screenshot/" + UUID1;
    public static final Session.ImageFormat IMAGE_FORMAT_PNG = Session.ImageFormat.PNG;

    public static final String METADATA_APPLICATION = "calc.exe";
    public static final String METADATA_USER_ACTION = "left click";
    public static final String METADATA_BROWSER = "Moodle page 1";
    public static final String METADATA_URL = "http://moodle.com/exam/1";
    public static final String METADATA_WINDOW_TITLE = "Moodle page 1";

    public static final int NR_OF_SCREENSHOTS = 120;


    //session data
    public static final int NUMBER_OF_LIVE_SESSIONS = 5;
    public static final int NUMBER_OF_LIVE_SESSIONS_EMPTY = 0;
    public static final int NUMBER_OF_SESSIONS = 5;
    public static final int NUMBER_OF_SESSIONS_EMPTY = 0;

    public static final List<Date> SESSION_SEARCH_DAY_LIST_FULL = Arrays.asList(DAY1, DAY2, DAY3, DAY4, DAY5, DAY6, DAY7, DAY8, DAY9);
    public static final List<Date> SESSION_SEARCH_DAY_LIST_FILTERED = Arrays.asList(DAY1, DAY2, DAY3, DAY4);
    public static final List<Date> SESSION_SEARCH_DAY_LIST_FILTERED_REVERSED = Arrays.asList(DAY4, DAY3, DAY2, DAY1);


    //paging data
    public static final int PAGE_NUMBER = 1;
    public static final int PAGE_SIZE = 9;
    public static final int PAGE_SIZE_EMPTY = 0;
    public static final String SORT_BY = "";
    public static final PageSortOrder SORT_ORDER = PageSortOrder.ASCENDING;


    //exam data
    public static final Long EXAM_ID = 1L;
    public static final String EXAM_UUID = "e7555417-382c-4200-99bb-7f80023cfeag";
    public static final String EXAM_NAME = "test exam";
    public static final String EXAM_DESCRIPTION = "test exam description";
    public static final String EXAM_URL = "http://url.com";
    public static final String EXAM_TYPE = "BYOD";


    //remaining data
    public static final FilterMap FILTER_MAP_EMPTY = new FilterMap();
}
