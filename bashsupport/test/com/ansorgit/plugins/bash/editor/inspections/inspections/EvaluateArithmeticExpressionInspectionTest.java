package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 29.12.10
 * Time: 12:33
 */
public class EvaluateArithmeticExpressionInspectionTest extends AbstractInspectionTestCase {
    public EvaluateArithmeticExpressionInspectionTest() {
        super(EvaluateArithmeticExpressionInspection.class);
    }

    public void testOk() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/ok", withOnTheFly(new EvaluateArithmeticExpressionInspection()));
    }

    public void testEvaluation() throws Exception {
        doTest("evaluateArithmeticExpressionInspection/evaluation", withOnTheFly(new EvaluateArithmeticExpressionInspection()));
    }
}
