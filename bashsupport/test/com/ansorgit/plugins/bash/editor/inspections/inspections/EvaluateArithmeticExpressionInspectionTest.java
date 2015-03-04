package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:33
 */
public class EvaluateArithmeticExpressionInspectionTest extends AbstractInspectionTestCase {
    public void testOk() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/ok", EvaluateArithmeticExpressionInspection.class, true);
    }

    public void testErrors() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/errors", EvaluateArithmeticExpressionInspection.class, true);
    }

    public void testEvaluation() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/evaluation", EvaluateArithmeticExpressionInspection.class, true);
    }
}
