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

package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.testFramework.PlatformTestUtil;

/**
 * @author jansorg
 */
//ignored, CI is too slow
public abstract class BashFoldingBuilderPerformanceTest extends LightBashCodeInsightFixtureTestCase {
    public void testFolding() {
        myFixture.configureByFile("functions_issue96.bash");
        myFixture.getEditor().getCaretModel().moveToOffset(myFixture.getEditor().getDocument().getTextLength());

        PlatformTestUtil.startPerformanceTest(getTestName(true), 10 * 250, () -> {
            for (int i = 0; i < 10; i++) {
                long start = System.currentTimeMillis();
                CodeFoldingManager.getInstance(getProject()).buildInitialFoldings(myFixture.getEditor());

                myFixture.type("echo hello world\n");

                System.out.printf("Cycle duration: %d\n", System.currentTimeMillis() - start);
            }
        }).usesAllCPUCores().assertTiming();
    }

    @Override
    protected String getBasePath() {
        return "editor/highlighting/syntaxHighlighter/performance";
    }

    @Override
    protected boolean isWriteActionRequired() {
        return false;
    }
}
