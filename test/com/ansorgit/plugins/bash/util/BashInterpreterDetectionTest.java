/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashInterpreterDetectionTest.java, Class: BashInterpreterDetectionTest
 * Last modified: 2010-07-22
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 22.07.2010
 * Time: 19:05:25
 */
public class BashInterpreterDetectionTest {
    @Test
    public void testFindBestLocation() throws Exception {
        BashInterpreterDetection detection = new BashInterpreterDetection() {
            @Override
            boolean isSuitable(String guessLocation) {
                return guessLocation.equals("/bin/bash");
            }
        };

        Assert.assertEquals("/bin/bash", detection.findBestLocation());
    }
}
