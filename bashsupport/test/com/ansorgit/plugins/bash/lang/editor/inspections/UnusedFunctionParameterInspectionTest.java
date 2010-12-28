package com.ansorgit.plugins.bash.lang.editor.inspections;

import com.ansorgit.plugins.bash.editor.inspections.inspections.UnusedFunctionParameterInspection;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 16:50
 */
public class UnusedFunctionParameterInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("unusedFunctionParameter/ok", new UnusedFunctionParameterInspection());
    }

    public void testUnusedFunctionParameter() throws Exception {
        doTest("unusedFunctionParameter/firstParamUnused", new UnusedFunctionParameterInspection());
    }

    public void testUnusedLastFunctionParameter() throws Exception {
        doTest("unusedFunctionParameter/lastParamUnused", new UnusedFunctionParameterInspection());
    }
}
