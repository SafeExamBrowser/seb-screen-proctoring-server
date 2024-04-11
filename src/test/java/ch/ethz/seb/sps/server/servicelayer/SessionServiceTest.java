package ch.ethz.seb.sps.server.servicelayer;

import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.service.Group;
import ch.ethz.seb.sps.domain.model.service.Session;
import ch.ethz.seb.sps.domain.model.service.Session.ImageFormat;
import ch.ethz.seb.sps.server.datalayer.dao.GroupDAO;
import ch.ethz.seb.sps.server.datalayer.dao.SessionDAO;
import ch.ethz.seb.sps.server.servicelayer.impl.ProctoringCacheService;
import ch.ethz.seb.sps.server.servicelayer.impl.SessionServiceImpl;
import ch.ethz.seb.sps.utils.Result;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SessionServiceTest {

    private static final Logger log = LoggerFactory.getLogger(SessionServiceTest.class);
    private final JSONMapper jsonMapper = new JSONMapper();

    private static final Long GENERIC_LONG = 1L;
    private static final String EMPTY_STRING = "";
    private static final Long ID = 777L;
    private static final Long GROUP_ID = 999L;
    private static final String GROUP_UUID = "e7555417-382c-4200-99bb-7f80023cfeaf";
    private static final String UUID = "be481b6a-bf1e-490e-9d18-c97355b01bfd";
    private static final String CLIENT_NAME = "seb_0d03539e-699d-48a3-8335-853800c5a1ff_3";
    private static final String CLIENT_IP = "127.0.0.1";
    private static final String CLIENT_MACHINE_NAME = "3.7.0 BETA (x64)";
    private static final String CLIENT_OS_NAME = "Windows 10, Microsoft Windows NT 10.0.19045.0 (x64)";
    private static final String CLIENT_VERSION = "3.7.0 BETA (x64)";
    private static final ImageFormat IMAGE_FORMAT_PNG = ImageFormat.PNG;
    private static final Long CREATION_TIME = 1712137768194L;
    private static final Long LAST_UPDATE_TIME = 1712137768194L;
    private static final Long TERMINATION_TIME = 1712220293956L;

    @Mock
    private GroupDAO groupDAO;
    @Mock
    private SessionDAO sessionDAO;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    private ProctoringCacheService proctoringCacheService;
    @InjectMocks
    private SessionServiceImpl sessionService;


    //-------sessions creation tests---------//
    @Test
    public void createSessionSucceedsWithGenericData() throws JsonProcessingException {
        //GIVEN
        Session expectedSession = createGenericSession(false);

        //WHEN
        when(this.groupDAO.existsByUUID(any()))
                .thenReturn(true);
        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createGenericGroup());
        when(this.sessionDAO.createNew(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(createGenericSessionResult(false));

        Result<Session> newSession = this.sessionService.createNewSession(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, IMAGE_FORMAT_PNG);

        //THEN
        assertFalse(newSession.hasError());
        assertEquals(this.jsonMapper.writeValueAsString(expectedSession), this.jsonMapper.writeValueAsString(newSession.get()));
    }

    @Test
    public void createSessionSucceedsWithRealisticData() throws JsonProcessingException {
        //GIVEN
        Session expectedSession = createRealisticSession(false, null, null);

        //WHEN
        when(this.groupDAO.existsByUUID(any()))
                .thenReturn(true);
        when(this.proctoringCacheService.getActiveGroup(any()))
                .thenReturn(createGenericGroup());
        when(this.sessionDAO.createNew(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), any()))
                .thenReturn(createRealisticSessionResult(false, null, null));

        Result<Session> newSession = this.sessionService.createNewSession(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, IMAGE_FORMAT_PNG);

        //THEN
        assertFalse(newSession.hasError());
        assertEquals(this.jsonMapper.writeValueAsString(expectedSession), this.jsonMapper.writeValueAsString(newSession.get()));
    }
    //-------------------------------//


    //-------session update tests---------//
    @Test
    public void updateSessionSucceedsWithGenericData() throws JsonProcessingException {
        //GIVEN
        Session expectedSession = createGenericSession(true);

        when(this.sessionDAO.byModelId(any()))
                .thenReturn(createGenericSessionResult(true));
        when(this.sessionDAO.save(any()))
                .thenReturn(createGenericSessionResult(true));

        //WHEN
        Result<Session> updatedSession = this.sessionService.updateSessionData(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);

        //THEN
        assertEquals(this.jsonMapper.writeValueAsString(expectedSession), this.jsonMapper.writeValueAsString(updatedSession.get()));
    }

    @Test
    public void updateSessionSucceedsWithRealisticData() throws JsonProcessingException {
        //GIVEN
        final String newClientIp = "127.0.0.2";
        final String newClientVersion = "7.0";

        Session expectedSession = createRealisticSession(true, newClientIp, newClientVersion);
        Result<Session> mockSession = createRealisticSessionResult(true, null, null);
        Result<Session> mockSessionUpdated = createRealisticSessionResult(true, newClientIp, newClientVersion);


        when(this.sessionDAO.byModelId(any()))
                .thenReturn(mockSession);
        when(this.sessionDAO.save(any()))
                .thenReturn(mockSessionUpdated);

        //WHEN
        Result<Session> updatedSession = this.sessionService.updateSessionData(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);

        //THEN
        assertEquals(this.jsonMapper.writeValueAsString(expectedSession), this.jsonMapper.writeValueAsString(updatedSession.get()));
    }

    @Test
    public void updateSessionFailsWithRealisticData() throws JsonProcessingException {
        //GIVEN
        final String newClientIp = "127.0.0.2";
        final String newClientVersion = "7.0";

        Session expectedSession = createRealisticSession(true, null, null);
        Result<Session> mockSession = createRealisticSessionResult(true, null, null);
        Result<Session> mockSessionUpdated = createRealisticSessionResult(true, newClientIp, newClientVersion);


        when(this.sessionDAO.byModelId(any()))
                .thenReturn(mockSession);
        when(this.sessionDAO.save(any()))
                .thenReturn(mockSessionUpdated);

        //WHEN
        Result<Session> updatedSession = this.sessionService.updateSessionData(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);

        //THEN
        assertNotEquals(this.jsonMapper.writeValueAsString(expectedSession), this.jsonMapper.writeValueAsString(updatedSession.get()));
    }
    //-------------------------------//


    //-------generic data---------//
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

    private Result<Session> createGenericSessionResult(boolean isActive){
        return Result.of(new Session(
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
        ));
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
                GENERIC_LONG,
                null
        );
    }
    //-------------------------------//

    //-------realistic data---------//
    private Session createRealisticSession(boolean isActive, String newClientIp, String newClientVersion){
        return new Session(
                ID,
                GROUP_ID,
                UUID,
                CLIENT_NAME,
                newClientIp!=null ? newClientIp : CLIENT_IP,
                newClientVersion!=null ? newClientVersion : CLIENT_VERSION,
                CLIENT_OS_NAME,
                CLIENT_VERSION,
                IMAGE_FORMAT_PNG,
                CREATION_TIME,
                LAST_UPDATE_TIME,
                isActive ? null : TERMINATION_TIME
        );
    }

    private Result<Session> createRealisticSessionResult(boolean isActive, String newClientIp, String newClientVersion){
        return Result.of(new Session(
                ID,
                GROUP_ID,
                UUID,
                CLIENT_NAME,
                newClientIp!=null ? newClientIp : CLIENT_IP,
                newClientVersion!=null ? newClientVersion : CLIENT_VERSION,
                CLIENT_OS_NAME,
                CLIENT_VERSION,
                IMAGE_FORMAT_PNG,
                CREATION_TIME,
                LAST_UPDATE_TIME,
                isActive ? null : TERMINATION_TIME
        ));
    }
    //-------------------------------//

}