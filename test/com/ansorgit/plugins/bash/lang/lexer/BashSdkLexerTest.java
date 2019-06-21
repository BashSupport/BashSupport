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

package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.BashVersion;
import com.intellij.lexer.Lexer;
import com.intellij.testFramework.LexerTestCase;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.nio.file.Paths;

/**
 * @author jansorg
 */
public class BashSdkLexerTest extends LexerTestCase {
    @Test
    public void testIssue118() {
        doTest("${x%.@(a|b)}");
    }

    @Override
    protected Lexer createLexer() {
        return new BashLexer(BashVersion.Bash_v4);
    }

    @Override
    protected String getDirPath() {
        return Paths.get(BashTestUtils.getBasePath(), "lexer").toString();
    }

    protected void doTest(@NonNls String text, @Nullable String expected, @NotNull Lexer lexer) {
        String result = printTokens(text, 0, lexer);

        if (expected != null) {
            assertSameLines(expected, result);
        }
        else {
            // changed from the original code in LexerTestCase
            // LexerTestCase is expecting that the test is part of the IntelliJ repository
            // BashSupport is not, so we get rid of the call to PathManager.getHomePath()
            assertSameLinesWithFile(getDirPath() + "/" + getTestName(true) + ".txt", result);
        }
    }
}
