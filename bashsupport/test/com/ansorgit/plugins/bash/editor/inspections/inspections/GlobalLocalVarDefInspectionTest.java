package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class GlobalLocalVarDefInspectionTest extends AbstractInspectionTestCase {

    public GlobalLocalVarDefInspectionTest() {
        super(GlobalLocalVarDefInspection.class);
    }

    public void testError() throws Exception {
        doTest("globalLocalVarDefInspection/error", new GlobalLocalVarDefInspection());
    }

    public void testOk() throws Exception {
        doTest("globalLocalVarDefInspection/ok", new GlobalLocalVarDefInspection());
    }
}