package ch.ethz.seb.sps.integrationtests.servicelayer;

import ch.ethz.seb.sps.domain.Domain;
import ch.ethz.seb.sps.domain.api.API;
import ch.ethz.seb.sps.domain.model.EntityKey;
import ch.ethz.seb.sps.domain.model.Page;
import ch.ethz.seb.sps.domain.model.service.ScheduledDelete;
import ch.ethz.seb.sps.domain.model.service.ScheduledDeleteInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestScheduledDeleteService extends ServiceTest {

    @Test
    public void i0_testModelCreation() throws JsonProcessingException {

        String jsonString = "{\"id\":1,\"state\":\"PENDING\",\"deleteDueTime\":1767222000000,\"scheduleTime\":1772622580642,\"info\":[{\"state\":\"PENDING\",\"examUuid\":\"a33d1f74-d5f2-47a3-8993-dc4d813bd4e4\"},{\"state\":\"PENDING\",\"examUuid\":\"a33d1f74-d5f2-47a3-8993-dc4d813bd4e5\"}]}";
        ScheduledDelete scheduledDelete = jsonMapper.readValue(jsonString, ScheduledDelete.class);
        assertTrue(scheduledDelete.getPK() == 1);
    }

    @Test
    public void i1_testRequestScheduledDelete() throws Exception {

        // due date is Wed Dec 31 2025 23:00:00 GMT+0000
        final long dueTime = 1767222000000L;

        Map<String, String> attributes = new HashMap<>();
        attributes.put(Domain.SCHEDULED_DELETE.ATTR_DELETE_DUE_TIME, String.valueOf(dueTime));
        attributes.put("institutionId", String.valueOf(1L));

        ScheduledDelete scheduledDelete = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_REQUEST_ENDPOINT)
                .withMethod(HttpMethod.GET)
                .withAttributes(attributes)
                .getAsObject(new TypeReference<ScheduledDelete>(){});


        assertNotNull(scheduledDelete);
        assertEquals(
                "ScheduledDelete{id=null, state=PENDING, deleteDueTime=1767222000000, scheduleTime=null, startTime=null, endTime=null, ownerUUID='super-admin', " +
                        "info=[ScheduledDeleteInfo{id=null, scheduledDeleteId=null, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', deletionInfo='{group_1_name=test_group, examName=test exam 01, examType=, group_1_uuid=3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c, group_1_sessionCount=2, group_2_name=exam_group, group_2_sessionCount=1, group_2_uuid=1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c}', errorInfo='null'}, " +
                        "ScheduledDeleteInfo{id=null, scheduledDeleteId=null, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e5', deletionInfo='{examName=test exam 02, examType=}', errorInfo='null'}, " +
                        "ScheduledDeleteInfo{id=null, scheduledDeleteId=null, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e6', deletionInfo='{examName=test exam 03, examType=}', errorInfo='null'}]}",
                scheduledDelete.toString());

    }

    @Test
    public void i2_testCreateScheduledDelete() throws Exception {
        // create
        final List<ScheduledDeleteInfo> info = new ArrayList<>();
        info.add(new ScheduledDeleteInfo(
                null, null, ScheduledDeleteInfo.State.PENDING, "a33d1f74-d5f2-47a3-8993-dc4d813bd4e4", null, null)
        );
        info.add(new ScheduledDeleteInfo(
                null, null, ScheduledDeleteInfo.State.PENDING, "a33d1f74-d5f2-47a3-8993-dc4d813bd4e5", null, null)
        );

        final ScheduledDelete scheduledDelete = new ScheduledDelete(
                null,
                ScheduledDelete.State.PENDING,
                1767222000000L,
                2524604400000L,
                null,
                null,
                null,
                info
        );

        final ScheduledDelete created = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_ENDPOINT)
                .withMethod(HttpMethod.POST)
                .withBodyJson(scheduledDelete)
                .getAsObject(new TypeReference<ScheduledDelete>(){});

        assertNotNull(created);
        assertEquals(
                "ScheduledDelete{id=1, state=PENDING, deleteDueTime=1767222000000, scheduleTime=2524604400000, startTime=null, endTime=null, ownerUUID='super-admin', " +
                        "info=[ScheduledDeleteInfo{id=1, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', deletionInfo='{group_1_name=test_group, examName=test exam 01, examType=, group_1_uuid=3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c, group_1_sessionCount=2, group_2_name=exam_group, group_2_sessionCount=1, group_2_uuid=1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c}', errorInfo='null'}, " +
                        "ScheduledDeleteInfo{id=2, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e5', deletionInfo='{examName=test exam 02, examType=}', errorInfo='null'}]}",
                created.toString());


        // Get single
        final ScheduledDelete single = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_ENDPOINT + "/1")
                .withMethod(HttpMethod.GET)
                .getAsObject(new TypeReference<ScheduledDelete>(){});

        assertNotNull(single);
        assertEquals(
                "ScheduledDelete{id=1, state=PENDING, deleteDueTime=1767222000000, scheduleTime=2524604400000, startTime=null, endTime=null, ownerUUID='super-admin', " +
                        "info=[ScheduledDeleteInfo{id=1, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', deletionInfo='{group_1_name=test_group, examName=test exam 01, examType=, group_1_uuid=3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c, group_1_sessionCount=2, group_2_name=exam_group, group_2_sessionCount=1, group_2_uuid=1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c}', errorInfo='null'}, " +
                        "ScheduledDeleteInfo{id=2, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e5', deletionInfo='{examName=test exam 02, examType=}', errorInfo='null'}]}",
                single.toString());

        // get list
        final Page<ScheduledDelete> page = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_ENDPOINT)
                .withMethod(HttpMethod.GET)
                .getAsObject(new TypeReference<Page<ScheduledDelete>>(){});

        assertNotNull(page);
        assertEquals(
                "Page [numberOfPages=1, pageNumber=1, pageSize=10, sort=null, content=[" +
                "ScheduledDelete{id=1, state=PENDING, deleteDueTime=1767222000000, scheduleTime=2524604400000, startTime=null, endTime=null, ownerUUID='super-admin', " +
                        "info=[ScheduledDeleteInfo{id=1, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e4', deletionInfo='{group_1_name=test_group, examName=test exam 01, examType=, group_1_uuid=3cfb99c0-34a5-4ffd-a11c-6d9790b3f24c, group_1_sessionCount=2, group_2_name=exam_group, group_2_sessionCount=1, group_2_uuid=1cfb88c0-34a5-4ffd-a11c-6d9790b3f24c}', errorInfo='null'}, " +
                        "ScheduledDeleteInfo{id=2, scheduledDeleteId=1, state=PENDING, examUUID='a33d1f74-d5f2-47a3-8993-dc4d813bd4e5', deletionInfo='{examName=test exam 02, examType=}', errorInfo='null'}]}" +
                        "]]",

                page.toString());

        // delete
        EntityKey deleted = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_ENDPOINT + "/1")
                .withMethod(HttpMethod.DELETE)
                .getAsObject(new TypeReference<EntityKey>(){});


        assertNotNull(deleted);
        assertEquals(
                "EntityKey [modelId=1, entityType=SCHEDULED_DELETE]",
                deleted.toString());

        // get empty page
        final Page<ScheduledDelete> emptyPage = super.getRestAPITestHelper()
                .withAccessToken(getSebAdminAccess())
                .withPath("/exam" + API.SCHEDULED_DELETE_ENDPOINT)
                .withMethod(HttpMethod.GET)
                .getAsObject(new TypeReference<Page<ScheduledDelete>>(){});

        assertNotNull(emptyPage);
        assertEquals(
                "Page [numberOfPages=0, pageNumber=1, pageSize=10, sort=null, content=[" +
                        "]]",

                emptyPage.toString());
    }
}
