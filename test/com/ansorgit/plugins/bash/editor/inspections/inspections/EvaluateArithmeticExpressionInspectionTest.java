package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:33
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
