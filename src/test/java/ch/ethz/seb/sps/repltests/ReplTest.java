/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.repltests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

import ch.ethz.seb.sps.utils.Utils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import ch.ethz.seb.sps.domain.api.API.PrivilegeType;
import ch.ethz.seb.sps.domain.api.JSONMapper;
import ch.ethz.seb.sps.domain.model.EntityType;
import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.domain.model.user.EntityPrivilege;
import ch.ethz.seb.sps.domain.model.user.UserPrivileges;
import io.swagger.v3.core.util.Constants;

public class ReplTest {

    @Test
    public void testPWDGen() {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        final String encode = bCryptPasswordEncoder.encode("test");
        System.out.print(encode);
    }
//
//    @Test
//    public void encodeURL() throws UnsupportedEncodingException {
//        String encoded = URLEncoder.encode("heute + morgen", "utf8");
//        
//        assertEquals("heute+%2B+morgen", encoded);
//        String decodedOrig = URLDecoder.decode(encoded, "utf8");
//        String decodedMe = Utils.decodeFormURL_UTF_8(encoded);
//        assertEquals("heute+++morgen", decodedMe);
//        assertEquals("heute + morgen", decodedOrig);
//
//        String encodedMacProbably = "heute+\\++morgen";
//        String decodedMacMe = Utils.decodeFormURL_UTF_8(encodedMacProbably);
//        assertEquals("heute+++morgen", decodedMacMe);
//    }


    @Test
    public void intCast() {
        final float max = 500;
        final int mappedMax = 10;
        assertEquals("0", String.valueOf((int) (20 / max * mappedMax)));
        assertEquals("0", String.valueOf((int) (49 / max * mappedMax)));
        assertEquals("1", String.valueOf((int) (50 / max * mappedMax)));
        assertEquals("3", String.valueOf((int) (199 / max * mappedMax)));
        assertEquals("4", String.valueOf((int) (200 / max * mappedMax)));

        assertEquals("5", String.valueOf((int) (299 / max * mappedMax)));
        assertEquals("6", String.valueOf((int) (300 / max * mappedMax)));

        assertEquals("7", String.valueOf((int) (399 / max * mappedMax)));
        assertEquals("8", String.valueOf((int) (400 / max * mappedMax)));

        assertEquals("9", String.valueOf((int) (499 / max * mappedMax)));
        assertEquals("10", String.valueOf((int) (500 / max * mappedMax)));
    }

    @Test
    @Ignore
    public void commaSeparatedNumberListGenerator() {
        final StringBuilder stringBuilder = new StringBuilder();

        for (int i = 2900; i < 2940; i++) {
            stringBuilder.append(i).append(Constants.COMMA);
        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        assertEquals("", stringBuilder.toString());
    }

    @Test
    public void testUserPrivilegesJSON() throws JsonProcessingException {
        final JSONMapper jsonMapper = new JSONMapper();
        final Map<EntityType, PrivilegeType> typePrivileges = new EnumMap<>(EntityType.class);
        typePrivileges.put(EntityType.USER, PrivilegeType.WRITE);
        typePrivileges.put(EntityType.CLIENT_ACCESS, PrivilegeType.WRITE);
        typePrivileges.put(EntityType.EXAM, PrivilegeType.WRITE);
        typePrivileges.put(EntityType.SEB_GROUP, PrivilegeType.MODIFY);
        typePrivileges.put(EntityType.SCREENSHOT, PrivilegeType.WRITE);
        typePrivileges.put(EntityType.SESSION, PrivilegeType.READ);
        typePrivileges.put(EntityType.SCREENSHOT_DATA, PrivilegeType.READ);

        final Collection<EntityPrivilege> entityPrivileges = new ArrayList<>();
        entityPrivileges.add(new EntityPrivilege(1L, EntityType.EXAM, 1L, "testUser", PrivilegeType.READ.flag));

        final UserPrivileges userPrivileges = new UserPrivileges("testUser", typePrivileges, entityPrivileges);

        final String jsonVal = jsonMapper.writeValueAsString(userPrivileges);
        assertEquals(
                "{\"uuid\":\"testUser\",\"typePrivileges\":{\"USER\":\"WRITE\",\"CLIENT_ACCESS\":\"WRITE\",\"EXAM\":\"WRITE\",\"SEB_GROUP\":\"MODIFY\",\"SESSION\":\"READ\",\"SCREENSHOT_DATA\":\"READ\",\"SCREENSHOT\":\"WRITE\"},\"entityPrivileges\":[{\"id\":1,\"entityType\":\"EXAM\",\"entityId\":1,\"userUuid\":\"testUser\",\"privileges\":\"r\"}]}",
                jsonVal);

        final UserPrivileges readValue = jsonMapper.readValue(jsonVal, UserPrivileges.class);
        assertNotNull(readValue);

    }

    @Test
    public void testJSONCriatorWithNoneProp() throws JsonMappingException, JsonProcessingException {
        final JSONMapper jsonMapper = new JSONMapper();
        final String json = "{\r\n"
                + "  \"name\": \"Demo Quiz 10 (MOCKUP)\",\r\n"
                + "  \"description\": \"Starts in a minute and ends after five minutes\",\r\n"
                + "  \"url\": \"http://lms.address.alias/api/\",\r\n"
                + "  \"type\": \"BYOD\",\r\n"
                + "  \"startTime\": 1695884353000,\r\n"
                + "  \"endTime\": 1695884653000\r\n"
                + "}";

        final Exam exam = jsonMapper.readValue(json, Exam.class);
        assertEquals(
                "Exam{id=null, uuid='null', name='Demo Quiz 10 (MOCKUP)', description='Starts in a minute and ends after five minutes', url='http://lms.address.alias/api/', type='BYOD', owner='null', supporter=[], creationTime=null, lastUpdateTime=null, terminationTime=null, startTime=1695884353000, endTime=1695884653000, deletionTime=null, entityPrivileges=null}",
                exam.toString());
    }

}
