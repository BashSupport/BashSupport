package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * @author jansorg
 */
public class RecursiveIncludeFileInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("recursiveIncludeFileInspection/ok", new RecursiveIncludeFileInspection());
    }

    public void testRecursiveInclusion() throws Exception {
        doTest("recursiveIncludeFileInspection/recursiveInclusion", new RecursiveIncludeFileInspection());
    }

    public void testDeepRecursiveInclusion() throws Exception {
        doTest("recursiveIncludeFileInspection/deepRecursiveInclusion", new RecursiveIncludeFileInspection());
    }
}
