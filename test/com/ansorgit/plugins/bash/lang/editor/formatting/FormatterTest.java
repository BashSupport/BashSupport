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

package com.ansorgit.plugins.bash.lang.editor.formatting;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.base.BashFormatterTestCase;
import com.ansorgit.plugins.bash.lang.base.TestUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Test suite for static formatting. Compares two files:
 * before and after formatting
 * <br>
 * (Based on the code from the GroovyPlugin).
 *
 * @author Ilya.Sergey, Joachim Ansorg
 */
public class FormatterTest extends BashFormatterTestCase {
    @Override
    protected String getBasePath() {
        return BashTestUtils.getBasePath() + "/formatter/";
    }

    protected void setUp() throws Exception {
        super.setUp();
        myTempSettings.CLASS_BRACE_STYLE = CodeStyleSettings.END_OF_LINE;
        myTempSettings.METHOD_BRACE_STYLE = CodeStyleSettings.END_OF_LINE;
        myTempSettings.BRACE_STYLE = CodeStyleSettings.END_OF_LINE;

        BashProjectSettings projectSettings = BashProjectSettings.storedSettings(getProject());
        projectSettings.setFormatterEnabled(true);
    }

    @Test
    public void testCommand() throws Throwable {
        doTest();
    }

    @Test
    public void testCommandWithParams() throws Throwable {
        doTest();
    }

    @Test
    public void testFunction() throws Throwable {
        doTest();
    }

    @Test
    public void testNestedBlocks() throws Throwable {
        doTest();
    }

    @Test
    public void testStrings() throws Throwable {
        doTest();
    }

    @Test
    public void testIfThenElse() throws Throwable {
        doTest();
    }

    @Test
    public void testConditionalCommand() throws Throwable {
        doTest();
    }

    @Test
    public void testBracketKeyword() throws Throwable {
        doTest();
    }

    @Test
    public void testVariables() throws Throwable {
        doTest();
    }

    @Test
    public void testCase() throws Throwable {
        doTest();
    }

    @Test
    public void testBackticks() throws Throwable {
        doTest();
    }

    @Test
    public void testFullTest() throws Throwable {
        doTest();
    }

    @Test
    public void testHeredoc() throws Throwable {
        doTest();
    }

    @Test
    public void testHeredoc2() throws Throwable {
        doTest();
    }

    @Test
    public void testHeredoc3() throws Throwable {
        doTest();
    }

    @Test
    public void testParamExpansion() throws Throwable {
        doTest();
    }

    @Test
    public void testEvalBlocks() throws Throwable {
        doTest();
    }

    @Test
    public void testFileRedirect() throws Throwable {
        doTest();
    }

    @Test
    public void testWhileLoop() throws Throwable {
        doTest();
    }

    @Test
    public void testProcessSubstitution() throws Throwable {
        //issue 459
        doTest();
    }

    @Test
    public void testSubshell() throws Throwable {
        doTest();
    }

    protected void doTest() throws Throwable {
        List<String> data = TestUtils.readInput(getBasePath() + getTestName(true) + ".test");
        Assert.assertTrue("Expected two data sets", data.size() == 2);
        checkFormatting(data.get(0), data.get(1));
    }
}
