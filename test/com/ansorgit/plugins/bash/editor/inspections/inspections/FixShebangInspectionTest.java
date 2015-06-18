package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class FixShebangInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("fixShebangInspection/ok", new FixShebangInspection());
    }

    @Test
    public void testNonEmptyFile() throws Exception {
        doTest("fixShebangInspection/nonEmptyFile", new FixShebangInspection());
    }

}