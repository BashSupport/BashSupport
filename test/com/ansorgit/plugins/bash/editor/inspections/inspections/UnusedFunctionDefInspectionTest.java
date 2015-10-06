package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class UnusedFunctionDefInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("unusedFunctionDefInspection/ok", UnusedFunctionDefInspection.class, false);
    }

    @Test
    public void testOk2() throws Exception {
        doTest("unusedFunctionDefInspection/ok2", UnusedFunctionDefInspection.class, false);
    }

    @Test
    public void testOk3() throws Exception {
        doTest("unusedFunctionDefInspection/ok3", UnusedFunctionDefInspection.class, false);
    }

    @Test
    public void testOk4() throws Exception {
        doTest("unusedFunctionDefInspection/ok4", UnusedFunctionDefInspection.class, false);
    }

    @Test
    public void testUnusedDef() throws Exception {
        doTest("unusedFunctionDefInspection/unusedDef", UnusedFunctionDefInspection.class, false);
    }

    @Test
    public void testIssue228() throws Exception {
        doTest("unusedFunctionDefInspection/issue228", UnusedFunctionDefInspection.class, false);
    }
}
