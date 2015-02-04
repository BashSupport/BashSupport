package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * @author jansorg
 */
public class MissingIncludeFileInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("missingIncludeFileInspection/ok", new MissingIncludeFileInspection());
    }

    public void testMissingFile() throws Exception {
        doTest("missingIncludeFileInspection/missingFile", new MissingIncludeFileInspection());
    }

    public void testIncludeDirectory() throws Exception {
        doTest("missingIncludeFileInspection/includeDirectory", new MissingIncludeFileInspection());
    }
}
