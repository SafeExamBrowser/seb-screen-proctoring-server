/*
 * Copyright (c) 2022 ETH Zürich, Educational Development and Technology (LET)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package ch.ethz.seb.sps.repltests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ReplTest {

    @Test
    public void testPWDGen() {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        final String encode = bCryptPasswordEncoder.encode("test");
        System.out.print(encode);
    }

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

}
