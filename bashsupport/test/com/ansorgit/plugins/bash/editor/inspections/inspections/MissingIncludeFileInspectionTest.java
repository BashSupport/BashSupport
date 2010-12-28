package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 21:23
 */
public class MissingIncludeFileInspectionTest extends AbstractInspectionTestCase {
    public void testMissingFile() throws Exception {
        doTest("missingIncludeFileInspection/missingFile", new MissingIncludeFileInspection());
    }

    public void testIncludeDirectory() throws Exception {
        doTest("missingIncludeFileInspection/includeDirectory", new MissingIncludeFileInspection());
    }
}
