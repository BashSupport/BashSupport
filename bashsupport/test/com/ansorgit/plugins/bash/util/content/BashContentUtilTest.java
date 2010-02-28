/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashContentUtilTest.java, Class: BashContentUtilTest
 * Last modified: 2010-02-23
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

package com.ansorgit.plugins.bash.util.content;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * User: jansorg
 * Date: Feb 22, 2010
 * Time: 9:35:13 PM
 */
public class BashContentUtilTest {
    @Test
    public void testBashContentUtil() {
        URL resource = getClass().getResource("/com/ansorgit/plugins/bash/util/content/scriptWithShebang.txt");
        Assert.assertNotNull(resource);

        File file = new File(resource.getFile());
        Assert.assertTrue(0.75d < BashContentUtil.computeBashProbability(file, 0.75d, null));
    }
}
