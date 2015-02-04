package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class ReadonlyVariableInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("readOnlyVariableInspection/ok", ReadonlyVariableInspection.class, false);
    }

    public void testWriteAttempt() throws Exception {
        doTest("readOnlyVariableInspection/writeAttempt", ReadonlyVariableInspection.class, false);
    }

    public void testRedefinition() throws Exception {
        doTest("readOnlyVariableInspection/redefinition", ReadonlyVariableInspection.class, false);
    }
}