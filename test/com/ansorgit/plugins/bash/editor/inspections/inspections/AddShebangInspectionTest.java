package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class AddShebangInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("addShebangInspection/ok", new AddShebangInspection());
    }

    @Test
    public void testEmptyFile() throws Exception {
        doTest("addShebangInspection/emptyFile", new AddShebangInspection());
    }

    @Test
    public void testNonEmptyFile() throws Exception {
        doTest("addShebangInspection/nonEmptyFile", new AddShebangInspection());
    }
}
