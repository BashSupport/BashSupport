package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class RecursiveIncludeFileInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("recursiveIncludeFileInspection/ok", new RecursiveIncludeFileInspection());
    }

    @Test
    public void testRecursiveInclusion() throws Exception {
        doTest("recursiveIncludeFileInspection/recursiveInclusion", new RecursiveIncludeFileInspection());
    }

    @Test
    public void testDeepRecursiveInclusion() throws Exception {
        doTest("recursiveIncludeFileInspection/deepRecursiveInclusion", new RecursiveIncludeFileInspection());
    }
}
