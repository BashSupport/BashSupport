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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.testFramework.PlatformTestUtil;
import com.intellij.util.ThrowableRunnable;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

//@Ignore
public abstract class BashSyntaxHighlighterPerformanceTest extends AbstractBashSyntaxHighlighterTest {
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

    @Test
    @Ignore
    public void testHighlightingPerformanceLarge() throws Exception {
        PlatformTestUtil.startPerformanceTest(getTestName(true), 10 * 2000, new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                doPerformanceTest("functions_issue96.bash", 10, 2000.0);
            }
        }).usesAllCPUCores().usesAllCPUCores().assertTiming();

        // With tuning:
        //      Finished highlighting 10/10, avg: 20538,100000 ms, min: 18969 ms, max: 22855 ms
    }

    @Test
    public void testHighlightingPerformanceSmall() throws Exception {
        PlatformTestUtil.startPerformanceTest(getTestName(true), 35 * 500, new ThrowableRunnable() {
            @Override
            public void run() throws Throwable {
                //Average: 550.4 ms
                doPerformanceTest("AlsaUtils.bash", 35, 500.0);
            }
        }).usesAllCPUCores().usesAllCPUCores().assertTiming();
    }

    protected void doPerformanceTest(String filename, int highlightingPasses, double maxTimeMillis) throws IOException, InterruptedException {
        MinMaxValue minMax = new MinMaxValue();

        //do one highlighting without counting it to start the plattform
        System.out.println("Starting...");
        myFixture.configureByFile(filename);
        myFixture.doHighlighting();

       // System.out.println("Attach ...");
       // Thread.sleep(5000);

        for (int i = 1; i <= highlightingPasses; i++) {
            System.out.println(String.format("Highlighting %d/%d,", i, highlightingPasses));
            myFixture.configureByFile(filename);

            long start = System.currentTimeMillis();
            myFixture.doHighlighting();
            minMax.add(System.currentTimeMillis() - start);

            System.out.println(String.format("Finished highlighting %d/%d, avg: %f ms, min: %d ms, max: %d ms", i, highlightingPasses, minMax.average(), minMax.min(), minMax.max()));
        }

        String message = String.format("Highlighting too slow. avg: %f ms, min: %d ms, max: %d ms", minMax.average(), minMax.min(), minMax.max());
        Assert.assertTrue(message, maxTimeMillis >= minMax.min());
    }
}
