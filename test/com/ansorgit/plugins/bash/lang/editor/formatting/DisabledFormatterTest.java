/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: FormatterTest.java, Class: FormatterTest
 * Last modified: 2010-02-10
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

package com.ansorgit.plugins.bash.lang.editor.formatting;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.base.BashFormatterTestCase;
import com.ansorgit.plugins.bash.lang.base.TestUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.psi.codeStyle.CodeStyleSettings;

import java.util.List;

/**
 * Test suite for static formatting. Compares two files:
 * before and after formatting
 * <p/>
 * (Based on the code from the GroovyPlugin).
 *
 * @author Ilya.Sergey, Joachim Ansorg
 */
public class DisabledFormatterTest extends BashFormatterTestCase {
    @Override
    protected String getBasePath() {
        return BashTestUtils.getBasePath() + "/disabledFormatter/";
    }

    protected void setUp() throws Exception {
        super.setUp();
        myTempSettings.CLASS_BRACE_STYLE = CodeStyleSettings.END_OF_LINE;
        myTempSettings.METHOD_BRACE_STYLE = CodeStyleSettings.END_OF_LINE;
        myTempSettings.BRACE_STYLE = CodeStyleSettings.END_OF_LINE;

        BashProjectSettings projectSettings = BashProjectSettings.storedSettings(getProject());
        projectSettings.setFormatterEnabled(false);
    }

    public void testDisabledFormatter() throws Throwable {
        doTest();
    }

    public void doTest() throws Throwable {
        final List<String> data = TestUtils.readInput(getBasePath() + getTestName(true) + ".test");
        checkFormatting(data.get(0), data.get(1));
    }
}
