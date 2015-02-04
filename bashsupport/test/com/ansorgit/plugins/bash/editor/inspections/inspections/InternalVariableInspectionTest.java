package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 19:47
 */
public class InternalVariableInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("internalVariableInspection/ok", new InternalVariableInspection());
    }

    public void testWriteAttempt() throws Exception {
        doTest("internalVariableInspection/writeAttempt", new InternalVariableInspection());
    }

    public void testRedefinition() throws Exception {
        doTest("internalVariableInspection/redefinition", new InternalVariableInspection());
    }
}
