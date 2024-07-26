package ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils;

import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.PageSortOrder;
import ch.ethz.seb.sps.domain.model.service.Session;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

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

    //screenshot metadata
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

    //session data
    public static final int NUMBER_OF_LIVE_SESSIONS = 5;
    public static final int NUMBER_OF_LIVE_SESSIONS_EMPTY = 0;
    public static final int NUMBER_OF_SESSIONS = 5;
    public static final int NUMBER_OF_SESSIONS_EMPTY = 0;

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
    public static final FilterMap FILTER_MAP = new FilterMap();
}
