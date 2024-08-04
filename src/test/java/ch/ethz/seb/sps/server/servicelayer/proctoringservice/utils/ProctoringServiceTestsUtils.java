package ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.ExamViewData;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.GroupViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.util.LinkedMultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CLIENT_IP;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CLIENT_MACHINE_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CLIENT_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CLIENT_OS_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CLIENT_VERSION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.CREATION_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EMPTY_STRING;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.END_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_DESCRIPTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_TYPE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_URL;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.EXAM_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.GENERIC_LONG;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.GROUP_DESCRIPTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.GROUP_ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.GROUP_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.GROUP_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.IMAGE_FORMAT_PNG;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.IMAGE_ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.IMAGE_LINK;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.LAST_UPDATE_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.METADATA_APPLICATION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.METADATA_BROWSER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.METADATA_URL;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.METADATA_USER_ACTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.METADATA_WINDOW_TITLE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.NR_OF_SCREENSHOTS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.NUMBER_OF_LIVE_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.NUMBER_OF_LIVE_SESSIONS_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.NUMBER_OF_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.NUMBER_OF_SESSIONS_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.PAGE_NUMBER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.PAGE_SIZE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.PAGE_SIZE_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.SESSION_SEARCH_DAY_LIST_FILTERED;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.SORT_BY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.SORT_ORDER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.START_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.TERMINATION_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.TIMESTAMP;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.UUID1;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestConstants.UUID_LIST;

public class ProctoringServiceTestsUtils {

    private final static JSONMapper jsonMapper = new JSONMapper();

    //--------ScreenshotsInGroupData---------
    public static ScreenshotsInGroupData createScreenshotGroupWithEmptyList() {
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
                new ExamViewData(null, null, null, null)
        );
    }

    public static ScreenshotsInGroupData createScreenshotGroup() {
        return new ScreenshotsInGroupData(
                GROUP_UUID,
                GROUP_NAME,
                GROUP_DESCRIPTION,
                NUMBER_OF_LIVE_SESSIONS,
                NUMBER_OF_SESSIONS,
                PAGE_NUMBER,
                PAGE_SIZE,
                SORT_BY,
                SORT_ORDER,
                createScreenshotViewDataList(),
                createExamViewData()
        );
    }

    public static List<ScreenshotSearchResult> createSearchScreenshotResultList(){
        List<ScreenshotSearchResult> screenshotSearchResultList = new ArrayList<>();
        for(int i = 0; i < SESSION_SEARCH_DAY_LIST_FILTERED.size(); i++){
            screenshotSearchResultList.add(new ScreenshotSearchResult(
                    IMAGE_ID,
                    createRealisticGroup(),
                    createSession(UUID_LIST.get(i)),
                    TIMESTAMP,
                    IMAGE_FORMAT_PNG,
                    createMetadataJsonMap()
            ));
        }

        return screenshotSearchResultList;
    }

    //---------------------------------------


    //-----------------Group-----------------
    public static Group createGenericGroup() {
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

    public static Group createRealisticGroup() {
        return new Group(
                GROUP_ID,
                GROUP_UUID,
                GROUP_NAME,
                GROUP_DESCRIPTION,
                EMPTY_STRING,
                CREATION_TIME,
                LAST_UPDATE_TIME,
                TERMINATION_TIME,
                EXAM_ID,
                null
        );
    }

    public static GroupViewData createRealisticGroupViewData() {
        return new GroupViewData(
                GROUP_ID,
                GROUP_UUID,
                GROUP_NAME,
                GROUP_DESCRIPTION,
                null,
                CREATION_TIME,
                LAST_UPDATE_TIME,
                TERMINATION_TIME,
                createExamViewData()
        );
    }
    //---------------------------------------


    //----------------Session----------------
    public static Collection<Session> createSessionList(){
        List<Session> sessionList = new ArrayList<>();
        for(int i = 0; i < SESSION_SEARCH_DAY_LIST_FILTERED.size(); i++){
            sessionList.add(createSession(UUID_LIST.get(i)));
        }

        return sessionList;
    }

    public static Session createGenericSession(boolean isActive) {
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

    public static Session createSession(String sessionUUID) {
        return new Session(
                ID,
                GROUP_ID,
                sessionUUID,
                CLIENT_NAME,
                CLIENT_IP,
                CLIENT_MACHINE_NAME,
                CLIENT_OS_NAME,
                CLIENT_VERSION,
                IMAGE_FORMAT_PNG,
                START_TIME,
                LAST_UPDATE_TIME,
                END_TIME
        );
    }

    public static List<SessionSearchResult> createSearchSessionResultList(){
        List<SessionSearchResult> searchSessionResultList = new ArrayList<>();
        for(int i = 0; i < SESSION_SEARCH_DAY_LIST_FILTERED.size(); i++){
            searchSessionResultList.add(createSessionSearchResult(UUID_LIST.get(i)));
        }

        return searchSessionResultList;
    }

    public static SessionSearchResult createSessionSearchResult(String sessionUUID){
        return new SessionSearchResult(
                GROUP_UUID,
                GROUP_NAME,
                CREATION_TIME,
                sessionUUID,
                START_TIME,
                END_TIME,
                CLIENT_NAME,
                CLIENT_IP,
                CLIENT_MACHINE_NAME,
                CLIENT_OS_NAME,
                CLIENT_VERSION,
                IMAGE_FORMAT_PNG,
                NR_OF_SCREENSHOTS,
                createExamViewData()
        );
    }
    //---------------------------------------


    //--------ScreenshotData & Token---------
    public static List<String> createLiveSessionTokenList() {
        List<String> liveSessionTokens = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_LIVE_SESSIONS; i++) {
            liveSessionTokens.add(UUID1);
        }

        return liveSessionTokens;
    }

    public static Map<String, ScreenshotDataRecord> createScreenshotDataRecordMap() throws JsonProcessingException {
        Map<String, ScreenshotDataRecord> screenshotDataRecordMap = new HashMap<>();

        for (int i = 0; i < NUMBER_OF_LIVE_SESSIONS; i++) {
            screenshotDataRecordMap.put(UUID_LIST.get(i), createScreenshotDataRecord(UUID_LIST.get(i)));
        }

        return screenshotDataRecordMap;
    }

    public static Collection<ScreenshotDataRecord> createScreenshotDataRecordList() throws JsonProcessingException {
        List<ScreenshotDataRecord> screenshotDataRecordList = new ArrayList<>();

        for (int i = 0; i < SESSION_SEARCH_DAY_LIST_FILTERED.size(); i++) {
            screenshotDataRecordList.add(createScreenshotDataRecord(UUID_LIST.get(i)));
        }

        return screenshotDataRecordList;
    }

    public static ScreenshotDataRecord createScreenshotDataRecord(final String sessionUUID) throws JsonProcessingException {
        return new ScreenshotDataRecord(
                ID,
                sessionUUID,
                TIMESTAMP,
                IMAGE_FORMAT_PNG.code,
                createMetadataJsonString()
        );
    }

    public static List<ScreenshotViewData> createScreenshotViewDataList(){
        List<ScreenshotViewData> screenshotViewDataList = new ArrayList<>();

        for(int i = 0; i < NUMBER_OF_LIVE_SESSIONS; i++){
            screenshotViewDataList.add(createScreenshotViewData(UUID_LIST.get(i)));
        }

        return screenshotViewDataList;
    }

    public static ScreenshotViewData createScreenshotViewData(String screenshotDataUUID){
        return new ScreenshotViewData(
                START_TIME,
                TIMESTAMP,
                END_TIME,
                screenshotDataUUID,
                CLIENT_NAME,
                CLIENT_IP,
                CLIENT_MACHINE_NAME,
                CLIENT_OS_NAME,
                CLIENT_VERSION,
                IMAGE_FORMAT_PNG,
                IMAGE_LINK,
                IMAGE_LINK + "/" + TIMESTAMP,
                createMetadataJsonMap()
        );
    }

    public static Map<String, String> createMetadataJsonMap(){
        Map<String, String> metadataMap = new HashMap<>();

        metadataMap.put(API.SCREENSHOT_META_DATA_APPLICATION, METADATA_APPLICATION);
        metadataMap.put(API.SCREENSHOT_META_DATA_USER_ACTION, METADATA_USER_ACTION);
        metadataMap.put(API.SCREENSHOT_META_DATA_BROWSER_TITLE, METADATA_BROWSER);
        metadataMap.put(API.SCREENSHOT_META_DATA_BROWSER_URL, METADATA_URL);
        metadataMap.put(API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE, METADATA_WINDOW_TITLE);

        return metadataMap;
    }

    public static String createMetadataJsonString() throws JsonProcessingException {
        String metadataString = "{";

        for (Map.Entry<String, String > metadata : createMetadataJsonMap().entrySet()) {
            String key = metadata.getKey();
            Object value = metadata.getValue();

            metadataString += '"' + key + '"' + ':' + '"' + value + '"' + ",";
        }

        metadataString = metadataString.substring(0, metadataString.length()-1);

        return metadataString + "}";
    }
    //---------------------------------------


    //-----------------Exam------------------
    public static Exam createExam(){
        return new Exam(
                EXAM_ID,
                EXAM_UUID,
                EXAM_NAME,
                EXAM_DESCRIPTION,
                EXAM_URL,
                EXAM_TYPE,
                null,
                new ArrayList<>(),
                CREATION_TIME,
                LAST_UPDATE_TIME,
                TERMINATION_TIME,
                START_TIME,
                END_TIME,
                null
        );
    }

    public static ExamViewData createExamViewData(){
        return new ExamViewData(
                EXAM_UUID,
                EXAM_NAME,
                START_TIME,
                END_TIME
        );
    }
    //---------------------------------------


    //---------------FilterMap---------------
    public static FilterMap createFilterMapWithMetadata(){
        LinkedMultiValueMap<String, String> filterMap = new LinkedMultiValueMap<>();

        filterMap.put(API.SCREENSHOT_META_DATA_APPLICATION, Arrays.asList(METADATA_APPLICATION));
        filterMap.put(API.SCREENSHOT_META_DATA_USER_ACTION, Arrays.asList(METADATA_USER_ACTION));
        filterMap.put(API.SCREENSHOT_META_DATA_BROWSER_TITLE, Arrays.asList(METADATA_BROWSER));
        filterMap.put(API.SCREENSHOT_META_DATA_BROWSER_URL, Arrays.asList(METADATA_URL));
        filterMap.put(API.SCREENSHOT_META_DATA_ACTIVE_WINDOW_TITLE, Arrays.asList(METADATA_WINDOW_TITLE));

        return new FilterMap(filterMap, null);
    }



}
