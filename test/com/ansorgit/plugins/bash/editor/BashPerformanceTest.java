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

package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.editor.highlighting.BashTestInspections;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.testFramework.PlatformTestUtil;

import java.util.ArrayList;

/**
 * @author jansorg
 */
//ignored, CI is too slow
public abstract class BashPerformanceTest extends LightBashCodeInsightFixtureTestCase {
    public void testEditorPerformance() {
        doTest(10);
    }

    // editorPerformanceLarge: 14078% longer. Expected: 407ms. Actual: 57705ms (57.7s). Timings: CPU=47 (23% of the etalon), I/O=13 (13% of the etalon), total=60 (13% of the etalon) 12 cores.
    public void testEditorPerformanceLarge() {
        doTest(100);
    }

    private void doTest(final int iterations) {
        myFixture.configureByFile("functions_issue96.bash");
        enableInspections();

        long start = System.currentTimeMillis();
        PlatformTestUtil.startPerformanceTest(getTestName(true), iterations * 2000, () -> {
            for (int i = 0; i < iterations; i++) {
                long innerStart = System.currentTimeMillis();
                Editor editor = myFixture.getEditor();
                editor.getCaretModel().moveToOffset(editor.getDocument().getTextLength());

                myFixture.type("\n");
                myFixture.type("echo \"hello world\"\n");
                myFixture.type("pri");
                myFixture.complete(CompletionType.BASIC);

                System.out.println("Cycle duration: " + (System.currentTimeMillis() - innerStart));
            }
        }).usesAllCPUCores().attempts(1).assertTiming();

        System.out.println("Complete duration: " + (System.currentTimeMillis() - start));
    }

    @Override
    protected String getBasePath() {
        return "editor/highlighting/syntaxHighlighter/performance";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        enableInspections();
    }

    private void enableInspections() {
        Class[] inspectionClasses = new BashTestInspections().getInspectionClasses();

        ArrayList<InspectionProfileEntry> inspections = new ArrayList<InspectionProfileEntry>();

        LocalInspectionEP[] extensions = Extensions.getExtensions(LocalInspectionEP.LOCAL_INSPECTION);
        for (LocalInspectionEP extension : extensions) {
            for (Class inspectionClass : inspectionClasses) {
                if (extension.implementationClass.equals(inspectionClass.getCanonicalName())) {
                    extension.enabledByDefault = true;

                    inspections.add(extension.instantiateTool());
                }
            }
        }

        myFixture.enableInspections(inspections.toArray(new InspectionProfileEntry[inspections.size()]));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (myFixture != null) {
            myFixture.tearDown();
        }
    }

}
