package com.ansorgit.plugins.bash.editor.inspections.inspections;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 20:02
 */
public class EvaluateExpansionInspectionTest extends AbstractInspectionTestCase {
    public void testEvaluation() throws Exception {
        doTest("evaluateExpansionInspection/evaluation", withOnTheFly(new EvaluateExpansionInspection()));
    }

    public void testFaultyExpression() throws Exception {
        doTest("evaluateExpansionInspection/faultyExpression", withOnTheFly(new EvaluateExpansionInspection()));
    }
}
