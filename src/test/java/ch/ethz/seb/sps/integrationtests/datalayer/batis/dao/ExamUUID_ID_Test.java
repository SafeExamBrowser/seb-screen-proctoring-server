/*
 * Copyright (c) 2025 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.integrationtests.datalayer.batis.dao;

import static org.junit.Assert.*;

import java.util.Collections;

import ch.ethz.seb.sps.domain.model.service.Exam;
import ch.ethz.seb.sps.integrationtests.ServiceTest_FULL_RDBMS;
import ch.ethz.seb.sps.server.datalayer.dao.ExamDAO;
import ch.ethz.seb.sps.utils.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ExamUUID_ID_Test extends ServiceTest_FULL_RDBMS  {

    @Autowired
    private ExamDAO examDAO;

    @Test
    // Text fix SEBSP-192 
    public void testExamCreationWithIntegerBasedUUIDs() throws Exception {

        Exam exam1 = new Exam(
                null,
                "123456789", // we use an integer number here for uuid like in the Olat case
                "OlatTestExam1",
                "OlatTestExam1",
                "https://OlatTestExam1",
                "OlatTest",
                "admin",
                Collections.emptyList(),
                null,
                null,
                null,
                0L,
                100L);

        // store exam1 with integer based uuid
        Result<Exam> saveExam = examDAO.createNew(exam1);
        assertFalse(saveExam.hasError());
        
        // do we get the right one back with byModelId?
        Result<Exam> examResult = examDAO.byModelId("123456789");
        assertFalse(examResult.hasError());
        Exam exam = examResult.get();
        assertEquals("OlatTestExam1", exam.name);
        
        // now we create an exam with uuid of former id to simulate the most vulnerable case
        // when getting the exam with modelId of this id we expect to get the one with the UUID
        // when getting the exam with pk, we expect to get the former exam where the PK match 
        // This then tests that when using modelId, modelId is always first interpreted as uuid and if no
        // exam with that uuid exists, it will interpret the modelId as PK and try it that way.
        Long PK = exam.getId();

        Exam exam2 = new Exam(
                null,
                String.valueOf(PK), // we use the PK integer number from former exam here
                "OlatTestExam2",
                "OlatTestExam2",
                "https://OlatTestExam2",
                "OlatTest",
                "admin",
                Collections.emptyList(),
                null,
                null,
                null,
                0L,
                100L);

        // store exam2 with integer based uuid
        Result<Exam> saveExam2 = examDAO.createNew(exam2);
        assertFalse(saveExam2.hasError());

        // when getting the exam with modelId of this id we expect to get the one with the UUID
        examResult = examDAO.byModelId(String.valueOf(PK));
        assertFalse(examResult.hasError());
        exam = examResult.get();
        assertEquals("OlatTestExam2", exam.name);

        // when getting the exam with pk, we expect to get the former exam where the PK match 
        examResult = examDAO.byPK(PK);
        assertFalse(examResult.hasError());
        exam = examResult.get();
        assertEquals("OlatTestExam1", exam.name);
    }
}
