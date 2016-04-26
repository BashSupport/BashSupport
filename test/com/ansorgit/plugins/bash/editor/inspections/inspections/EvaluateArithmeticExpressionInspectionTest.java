package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * @author jansorg
 */
public class EvaluateArithmeticExpressionInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testOk() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/ok", EvaluateArithmeticExpressionInspection.class, true);
    }

    @Test
    public void testErrors() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/errors", EvaluateArithmeticExpressionInspection.class, true);
    }

    @Test
    public void testEvaluation() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/evaluation", EvaluateArithmeticExpressionInspection.class, true);
    }
}
