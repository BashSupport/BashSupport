/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SystemPathUtilTest.java, Class: SystemPathUtilTest
 * Last modified: 2010-05-08
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

import junit.framework.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * User: jansorg
 * Date: 08.05.2010
 * Time: 12:07:32
 */
public class SystemPathUtilTest {
    @Test
    public void testFindBestExecutable() throws Exception {
        String path = SystemPathUtil.findBestExecutable("info");
        Assert.assertNotNull(path);
        //Assert.assertEquals("/usr/bin/info", path);
    }

    @Test
    public void testFindBestExecutablePaths() throws Exception {
        String path = SystemPathUtil.findBestExecutable("info", Arrays.asList("/usr/bin"));
        Assert.assertNotNull(path);

        path = SystemPathUtil.findBestExecutable("info", Arrays.asList("/path/which/is/not/here"));
        Assert.assertNull(path);
    }
}
