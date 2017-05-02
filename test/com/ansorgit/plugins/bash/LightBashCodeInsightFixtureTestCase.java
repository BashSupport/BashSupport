/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ansorgit.plugins.bash;

import com.intellij.openapi.util.SystemInfo;
import com.intellij.psi.PsiElement;
import com.intellij.testFramework.TestDataFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.IOException;

public abstract class LightBashCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase implements BashTestCase {

    protected PsiElement configurePsiAtCaret() {
        return configurePsiAtCaret(getTestName(true) + ".bash");
    }

    protected PsiElement configurePsiAtCaret(String fileNameInTestPath) {
        return BashTestUtils.configureFixturePsiAtCaret(fileNameInTestPath, myFixture);
    }

    /**
     * Return relative path to the test data. Path is relative to the
     * {@link com.intellij.openapi.application.PathManager#getHomePath()}
     *
     * @return relative path to the test data.
     */
    @NonNls
    protected String getBasePath() {
        return "";
    }

    /**
     * Return absolute path to the test data. Not intended to be overridden.
     *
     * @return absolute path to the test data.
     */
    @NonNls
    public final String getTestDataPath() {
        String basePath = getBasePath();
        if (SystemInfo.isWindows) {
            basePath = StringUtils.replace(basePath, "/", File.separator);
        }

        return BashTestUtils.getBasePath() + (basePath.startsWith(File.separator) ? "" : File.separator) + basePath;
    }

    public String loadTestDataFile(@TestDataFile String path) throws IOException {
        return BashTestUtils.loadTestCaseFile(this, path);
    }
}