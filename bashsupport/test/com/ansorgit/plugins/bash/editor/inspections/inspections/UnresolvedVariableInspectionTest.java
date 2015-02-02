package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class UnresolvedVariableInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("unresolvedVariableInspection/ok", UnresolvedVariableInspection.class, false);
    }

    public void testWarning() throws Exception {
        doTest("unresolvedVariableInspection/warning", UnresolvedVariableInspection.class, false);
    }
}