package com.ansorgit.plugins.bash.editor.highlighting;

import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.openapi.extensions.Extensions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;

//@Ignore
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

        if (myFixture != null) {
            myFixture.tearDown();
        }
    }

    @Test
    public void testHighlightingPerformanceLarge() throws Exception {
        doPerformanceTest("functions_issue96.bash", 10, 2000.0);

        // With tuning:
        //      Finished highlighting 10/10, avg: 20538,100000 ms, min: 18969 ms, max: 22855 ms
    }

    @Test
    public void testHighlightingPerformanceSmall() throws Exception {
        //Average: 550.4 ms
        doPerformanceTest("AlsaUtils.bash", 35, 500.0);
    }

    protected void doPerformanceTest(String filename, int highlightingPasses, double maxTimeMillis) throws IOException, InterruptedException {
        MinMaxValue minMax = new MinMaxValue();

        //do one highlighting without counting it to start the plattform
        System.out.println("Starting...");
        myFixture.configureByFile(filename);
        myFixture.doHighlighting();

        System.out.println("Attach ...");
        Thread.sleep(5000);

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
