package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class MissingIncludeFileInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("missingIncludeFileInspection/ok", new MissingIncludeFileInspection());
    }

    @Test
    public void testMissingFile() throws Exception {
        doTest("missingIncludeFileInspection/missingFile", new MissingIncludeFileInspection());
    }

    @Test
    public void testIncludeDirectory() throws Exception {
        doTest("missingIncludeFileInspection/includeDirectory", new MissingIncludeFileInspection());
    }
}
