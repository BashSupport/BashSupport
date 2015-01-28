package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class FixShebangInspectionTest extends AbstractInspectionTestCase {

    public FixShebangInspectionTest() {
        super(FixShebangInspection.class);
    }

    public void testOk() throws Exception {
        doTest("fixShebangInspection/ok", new FixShebangInspection());
    }

    public void testNonEmptyFile() throws Exception {
        doTest("fixShebangInspection/nonEmptyFile", new FixShebangInspection());
    }

}