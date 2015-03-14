package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class AddShebangInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("addShebangInspection/ok", new AddShebangInspection());
    }

    public void testEmptyFile() throws Exception {
        doTest("addShebangInspection/emptyFile", new AddShebangInspection());
    }

    public void testNonEmptyFile() throws Exception {
        doTest("addShebangInspection/nonEmptyFile", new AddShebangInspection());
    }
}
