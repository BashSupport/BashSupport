package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class GlobalLocalVarDefInspectionTest extends AbstractInspectionTestCase {
    public void testError() throws Exception {
        doTest("globalLocalVarDefInspection/error", GlobalLocalVarDefInspection.class, false);
    }

    public void testErrorLocalRedef() throws Exception {
        doTest("globalLocalVarDefInspection/errorLocalRedef", GlobalLocalVarDefInspection.class, false);
    }

    public void testOk() throws Exception {
        doTest("globalLocalVarDefInspection/ok", GlobalLocalVarDefInspection.class, false);
    }
}