package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.lang.BashLanguage;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.Extensions;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class BashSyntaxHighlighterPerformanceTest extends AbstractBashSyntaxHighlighterTest {
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

        myFixture.tearDown();
    }

    @Test
    public void testHighlightingPerformance() throws Exception {
        doPerformanceTest("functions_issue96.bash", 10);
    }

    protected void doPerformanceTest(String filename, int highlightingPasses) {
        long time = 0;
        for (int i = 1; i <= highlightingPasses; i++) {
            System.out.println(String.format("Highlighting %d/%d,", i, highlightingPasses));
            myFixture.configureByFile(filename);

            long start = System.currentTimeMillis();
            myFixture.doHighlighting();
            time += System.currentTimeMillis() - start;

            System.out.println(String.format("Finished highlighting %d/%d, avg: %f", i, highlightingPasses, ((float)time) / i));
        }

        float average = (float) time / highlightingPasses;
        Assert.assertTrue("Highlighting time exceeded. Average: " + average + " ms", 2000.0 >= average);
    }
}
