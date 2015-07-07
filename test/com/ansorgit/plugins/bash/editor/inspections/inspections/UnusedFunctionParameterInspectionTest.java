package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class UnusedFunctionParameterInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("unusedFunctionParameter/ok", new UnusedFunctionParameterInspection());
    }

    @Test
    public void testUnusedFunctionParameter() throws Exception {
        doTest("unusedFunctionParameter/firstParamUnused", new UnusedFunctionParameterInspection());
    }

    @Test
    public void testUnusedLastFunctionParameter() throws Exception {
        doTest("unusedFunctionParameter/lastParamUnused", new UnusedFunctionParameterInspection());
    }
}
