/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractResolveTest.java, Class: AbstractResolveTest
 * Last modified: 2010-06-30
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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.intellij.psi.PsiReference;
import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 21:09:35
 */
public abstract class AbstractResolveTest extends com.intellij.testFramework.ResolveTestCase {
    protected PsiReference configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash");
    }

    protected String getBasePath() {
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
