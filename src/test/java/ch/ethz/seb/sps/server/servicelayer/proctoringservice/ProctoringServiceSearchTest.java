package ch.ethz.seb.sps.server.servicelayer.proctoringservice;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.FilterMap;
import ch.ethz.seb.sps.domain.model.service.ScreenshotSearchResult;
import ch.ethz.seb.sps.domain.model.service.SessionSearchResult;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.ScreenshotDataDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.FILTER_MAP_EMPTY;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.NR_OF_SCREENSHOTS;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.SESSION_SEARCH_DAY_LIST_FILTERED;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.SESSION_SEARCH_DAY_LIST_FILTERED_REVERSED;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.SESSION_SEARCH_DAY_LIST_FULL;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceConstants.UUID_LIST;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createFilterMapWithMetadata;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createRealisticGroup;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createRealisticGroupViewData;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createScreenshotDataRecordList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createScreenshotSearchResultList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createSession;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createSessionList;
import static ch.ethz.seb.sps.server.servicelayer.proctoringservice.utils.ProctoringServiceTestsUtils.createSessionSearchResultList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProctoringServiceSearchTest {

    private final JSONMapper jsonMapper = new JSONMapper();

    @Mock
    private SessionDAO sessionDAO;

    @Mock
    private ScreenshotDataDAO screenshotDataDAO;

    @Mock
    private GroupDAO groupDAO;

    @InjectMocks
    private ProctoringServiceImpl proctoringService;

    static FilterMap filterMap;

    @BeforeAll
    public static void setup(){
        filterMap = createFilterMapWithMetadata();
    }

    //--------1st search layer - get day list---------
    @Test
    public void testGetDayListSessionSearch_NoMetadata(){
        //GIVEN
        List<Date> excpectedSessionSearchDayList = SESSION_SEARCH_DAY_LIST_FULL;
        mockDependenciesForSessionSearchDayList();

        //WHEN
        Result<List<Date>> sessionSearchDayList = this.proctoringService.queryMatchingDaysForSessionSearch(FILTER_MAP_EMPTY);

        //THEN
        assertFalse(sessionSearchDayList.hasError());
        assertEquals(excpectedSessionSearchDayList, sessionSearchDayList.getOrThrow());
    }

    @Test
    public void testGetDayListSessionSearch_WithMetadata(){
        //GIVEN
        List<Date> excpectedSessionSearchDayList = SESSION_SEARCH_DAY_LIST_FILTERED;
        mockDependenciesForSessionSearchDayList();
        mockDependenciesForSessionSearchDayList_WithMetadata();

        //WHEN
        Result<List<Date>> sessionSearchDayList = this.proctoringService.queryMatchingDaysForSessionSearch(filterMap);

        //THEN
        assertFalse(sessionSearchDayList.hasError());
        assertEquals(excpectedSessionSearchDayList, sessionSearchDayList.getOrThrow());
    }

    private void mockDependenciesForSessionSearchDayList(){
        when(this.sessionDAO.queryMatchingDaysForSessionSearch(any()))
                .thenReturn(Result.of(SESSION_SEARCH_DAY_LIST_FULL));
    }

    private void mockDependenciesForSessionSearchDayList_WithMetadata(){
        when(this.screenshotDataDAO.selectMatchingScreenshotDataPerDay(any()))
                .thenReturn(Result.of(SESSION_SEARCH_DAY_LIST_FILTERED_REVERSED));
    }
    //------------------------------------------------


    //--------2nd search layer - get sessions---------
    @Test
    public void testSearchSession() throws JsonProcessingException {
        //GIVEN
        Collection<SessionSearchResult> expectedSessionSearchResult = createSessionSearchResultList();
        mockDependenciesForSearchSession();

        //WHEN
        Result<Collection<SessionSearchResult>> sessionSearchResult = this.proctoringService.searchSessions(filterMap);

        //THEN
        assertEquals(this.jsonMapper.writeValueAsString(expectedSessionSearchResult), this.jsonMapper.writeValueAsString(sessionSearchResult.getOrThrow()));
    }

    private void mockDependenciesForSearchSession(){
        when(this.sessionDAO.allMatching(any()))
                .thenReturn(Result.of(createSessionList()));

        when(this.sessionDAO.getNumberOfScreenshots(any(), any()))
                .thenReturn((long)NR_OF_SCREENSHOTS);

        when(this.groupDAO.getGroupWithExamData(any()))
                .thenReturn(Result.of(createRealisticGroupViewData()));
    }
    //------------------------------------------------


    //--------3rd search layer - get screenshots---------
    @Test
    public void testSearchScreenshots() throws JsonProcessingException {
        //GIVEN
        List<ScreenshotSearchResult> expectedScreenshotSearchResult = createScreenshotSearchResultList();
        mockDependenciesForScreenshotSearch();

        //WHEN
        Result<Collection<ScreenshotSearchResult>> screenshotSearchResultsCollection = this.proctoringService.searchScreenshots(filterMap);
        List<ScreenshotSearchResult> screenshotSearchResult = (List<ScreenshotSearchResult>) screenshotSearchResultsCollection.getOrThrow();

        //THEN
        //compare singles values as metadata json as string is in different form
        assertEquals(expectedScreenshotSearchResult.size(), screenshotSearchResult.size());

        for(int i = 0; i < expectedScreenshotSearchResult.size(); i++){
            assertEquals(expectedScreenshotSearchResult.get(i).groupUUID, screenshotSearchResult.get(i).groupUUID);
            assertEquals(expectedScreenshotSearchResult.get(i).groupCreationTime, screenshotSearchResult.get(i).groupCreationTime);
            assertEquals(expectedScreenshotSearchResult.get(i).groupName, screenshotSearchResult.get(i).groupName);
            assertEquals(expectedScreenshotSearchResult.get(i).clientOSName, screenshotSearchResult.get(i).clientOSName);
        }

        //compare metadata
        for(int i = 0; i < expectedScreenshotSearchResult.size(); i++){
            Map<String, String> excpectedMetadata = expectedScreenshotSearchResult.get(i).metaData;
            Map<String, String> metadata = screenshotSearchResult.get(i).metaData;

            assertEquals(this.jsonMapper.writeValueAsString(excpectedMetadata), metadata.get("data"));
        }
    }

    private void mockDependenciesForScreenshotSearch() throws JsonProcessingException {
        when(this.screenshotDataDAO.searchScreenshotData(filterMap))
                .thenReturn(Result.of(createScreenshotDataRecordList()));

        for(int i = 0; i < SESSION_SEARCH_DAY_LIST_FILTERED.size(); i++){
            when(this.sessionDAO.byModelId(any()))
                    .thenReturn(Result.of(createSession(UUID_LIST.get(i))));

            when(this.groupDAO.byPK(any()))
                    .thenReturn(Result.of(createRealisticGroup()));
        }

    }
    //------------------------------------------------


    //--------all layers combined---------
    //usual use case is the combined search
    @Test
    public void testCompleteSearch(){
        //search criteria is always the same as the user only enters the search form once

        //GIVEN
        //1. retrieve date list
        List<Date> excpectedSessionSearchDayList = SESSION_SEARCH_DAY_LIST_FULL;

        //2. search sessions in the given time frame
        Collection<SessionSearchResult> expectedSessionSearchResult = createSessionSearchResultList();

        //3. retrieve screenshot for the given day
        List<ScreenshotSearchResult> expectedScreenshotSearchResult = createScreenshotSearchResultList();





    }





    //------------------------------------------------
}