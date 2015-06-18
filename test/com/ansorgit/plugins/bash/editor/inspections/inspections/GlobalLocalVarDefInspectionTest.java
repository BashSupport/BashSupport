package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class GlobalLocalVarDefInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testError() throws Exception {
        doTest("globalLocalVarDefInspection/error", GlobalLocalVarDefInspection.class, false);
    }

    @Test
    public void testErrorLocalRedef() throws Exception {
        doTest("globalLocalVarDefInspection/errorLocalRedef", GlobalLocalVarDefInspection.class, false);
    }

    @Test
    public void testOk() throws Exception {
        doTest("globalLocalVarDefInspection/ok", GlobalLocalVarDefInspection.class, false);
    }
}