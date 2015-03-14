package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:53
 */
public class RecursiveIncludeFileInspectionTest extends AbstractInspectionTestCase {
    public RecursiveIncludeFileInspectionTest() {
        super(RecursiveIncludeFileInspection.class);
    }

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
