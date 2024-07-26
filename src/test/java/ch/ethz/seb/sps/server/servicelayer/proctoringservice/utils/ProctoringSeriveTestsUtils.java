package ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils;

import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.service.ExamViewData;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.ScreenshotViewData;
import ch.ethz.seb.sps.domain.model.service.ScreenshotsInGroupData;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.server.datalayer.batis.model.ScreenshotDataRecord;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CLIENT_IP;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CLIENT_MACHINE_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CLIENT_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CLIENT_OS_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CLIENT_VERSION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.CREATION_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EMPTY_STRING;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.END_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_DESCRIPTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_TYPE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_URL;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.EXAM_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GENERIC_LONG;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GROUP_DESCRIPTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GROUP_ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GROUP_NAME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.GROUP_UUID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.ID;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.IMAGE_FORMAT_PNG;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.IMAGE_LINK;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.LAST_UPDATE_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.METADATA_APPLICATION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.METADATA_BROWSER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.METADATA_URL;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.METADATA_USER_ACTION;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.METADATA_WINDOW_TITLE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.NUMBER_OF_LIVE_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.NUMBER_OF_LIVE_SESSIONS_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.NUMBER_OF_SESSIONS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.NUMBER_OF_SESSIONS_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_NUMBER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_SIZE;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.PAGE_SIZE_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.SORT_BY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.SORT_ORDER;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.START_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.TERMINATION_TIME;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.TIMESTAMP;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.UUID1;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.Constants.UUID_LIST;

public class ProctoringSeriveTestsUtils {

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
                new ExamViewData(EXAM_UUID, EXAM_NAME, START_TIME, END_TIME)
        );
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
    //---------------------------------------


    //----------------Session----------------
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


}
