/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTestUtils.java, Class: BashTestUtils
 * Last modified: 2010-07-01
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

package com.ansorgit.plugins.bash;

import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: 01.07.2010
 * Time: 18:50:47
 * To change this template use File | Settings | File Templates.
 */
public class BashTestUtils {
    public static String getBasePath() {
        String configuredDir = StringUtils.stripToNull(System.getenv("BASHSUPPORT_TESTDATA"));

        if (configuredDir != null) {
            File dir = new File(configuredDir);
            if (dir.isDirectory() && dir.exists()) {
                return dir.getAbsolutePath();
            }
        }

        return System.getenv("HOME") + "/Projekte/JavaProjekte/BashSupport-googlecode/testData";
    }
}
