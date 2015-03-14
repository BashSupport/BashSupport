package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class UnusedFunctionDefInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("unusedFunctionDefInspection/ok", UnusedFunctionDefInspection.class, false);
    }

    public void testOk2() throws Exception {
        doTest("unusedFunctionDefInspection/ok2", UnusedFunctionDefInspection.class, false);
    }

    public void testOk3() throws Exception {
        doTest("unusedFunctionDefInspection/ok3", UnusedFunctionDefInspection.class, false);
    }

    public void testOk4() throws Exception {
        doTest("unusedFunctionDefInspection/ok4", UnusedFunctionDefInspection.class, false);
    }

    public void testUnusedDef() throws Exception {
        doTest("unusedFunctionDefInspection/unusedDef", UnusedFunctionDefInspection.class, false);
    }
}
