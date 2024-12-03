/*
 * Copyright (c) 2024 ETH Zürich, IT Services
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

public class UtilsTest {
    
    @Test
    @Ignore // TODO line brakes do not match on different systems
    public void testTrimJSONMap() {
        String value = """
                {
                   "screenProctoringMetadataApplication":"SafeExamBrowser.Client.exe (SafeExamBrowser.Client.exe)",
                   "screenProctoringMetadataBrowser":"Main Window: SEB Server (https://ralph.ethz.ch/), Additional Window: SEB Screen Proctoring (http://ralph.ethz.ch:3000/recording/04c59492-6221-4ded-917e-6fd8650649df)",
                   "screenProctoringMetadataURL":"https://ralph.ethz.ch/, http://ralph.ethz.ch:3000/recording/04c59492-6221-4ded-917e-6fd8650649df",
                   "screenProctoringMetadataUserAction":"Left mouse button has been released at (2087/961).",
                   "screenProctoringMetadataWindowTitle":"Applikations-Protokoll"
                }""";

        assertEquals(
                "{\r\n" +
                        "  \"screenProctoringMetadataApplication\" : \"SafeExamBrowser.Client.exe (SafeExamBrowser.Client.exe)\",\r\n" +
                        "  \"screenProctoringMetadataBrowser\" : \"Main Window: SEB Server (https://ralph.ethz.ch/), Additional Window: SEB Screen Proctoring (http://ralph.ethz.ch:3000/recording/04c59492-6221-4ded-917e-6fd8650649df)\",\r\n" +
                        "  \"screenProctoringMetadataURL\" : \"https://ralph.ethz.ch/, http://ralph.ethz.ch:3000/recording/04c59492-6221-4ded-917e-6fd8650649df\",\r\n" +
                        "  \"screenProctoringMetadataUserAction\" : \"Left mouse button has been released at (2087/961).\",\r\n" +
                        "  \"screenProctoringMetadataWindowTitle\" : \"Applikations-Protokoll\"\r\n" +
                        "}",
                Utils.prettyPrintJSON(Utils.trimJSONMap(value, 1000, 10)));
        
        assertEquals(
            "{\r\n" +
                    "  \"screenProctoringMetadataApplication\" : \"SafeExamBrowser.Client.exe (SafeExamBrowser.Client.exe)\",\r\n" +
                    "  \"screenProctoringMetadataUserAction\" : \"Left mouse button has been released at (2087/961).\",\r\n" +
                    "  \"screenProctoringMetadataBrowser\" : \"Main Window: SEB Server (https://ralph.ethz.ch/), Additional Window: SEB Scre...\",\r\n" +
                    "  \"screenProctoringMetadataURL\" : \"https://ralph.ethz.ch/, http://ralph.ethz.ch:3000/recording/04c59492-6221-4de...\",\r\n" +
                    "  \"screenProctoringMetadataWindowTitle\" : \"Applikations-Protokoll\"\r\n" +
                    "}", 
                 Utils.prettyPrintJSON(Utils.trimJSONMap(value, 500, 10)));

        assertEquals(
                "{\r\n" +
                        "  \"screenProctoringMetadataApplication\" : \"SafeExamBrowser.C...\",\r\n" +
                        "  \"screenProctoringMetadataUserAction\" : \"Left mouse button...\",\r\n" +
                        "  \"screenProctoringMetadataBrowser\" : \"Main Window: SEB ...\",\r\n" +
                        "  \"screenProctoringMetadataURL\" : \"https://ralph.eth...\",\r\n" +
                        "  \"screenProctoringMetadataWindowTitle\" : \"Applikations-Prot...\"\r\n" +
                        "}",
                Utils.prettyPrintJSON(Utils.trimJSONMap(value, 300, 10)));

        // Note if the max value is too small to fit all json attribute names, the utility returns null. 
        assertEquals(
                null,
                Utils.prettyPrintJSON(Utils.trimJSONMap(value, 200, 10)));
    }
    
    
}
