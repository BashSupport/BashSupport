/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SystemInfopageDocSourceTest.java, Class: SystemInfopageDocSourceTest
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

package com.ansorgit.plugins.bash.documentation;

import junit.framework.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * User: jansorg
 * Date: 08.05.2010
 * Time: 12:04:49
 */
public class SystemInfopageDocSourceTest {
    @Test
    public void testInfoPageCall() throws IOException {
        SystemInfopageDocSource source = new SystemInfopageDocSource();
        String bashInfoPage = source.loadPlainTextInfoPage("bash");

        Assert.assertNotNull(bashInfoPage);
        Assert.assertTrue(bashInfoPage.length() > 500);

        String html = source.callTextToHtml("abc");
        Assert.assertNotNull(html);
        Assert.assertTrue(html.contains("abc"));

        html = source.callTextToHtml(bashInfoPage);
        Assert.assertNotNull(html);
        Assert.assertTrue(html.length() > 500);
    }

    @Test
    public void testInfoFileExists() throws IOException {
        SystemInfopageDocSource source = new SystemInfopageDocSource();
        Assert.assertTrue(source.infoFileExists("info"));
        Assert.assertFalse(source.infoFileExists("thisCommandDoesNotExist"));
    }
}
