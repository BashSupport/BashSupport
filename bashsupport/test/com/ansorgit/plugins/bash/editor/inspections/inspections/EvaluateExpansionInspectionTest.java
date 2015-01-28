package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class EvaluateExpansionInspectionTest extends AbstractInspectionTestCase {
    public void testEvaluation() throws Exception {
        doTest("evaluateExpansionInspection/evaluation", withOnTheFly(new EvaluateExpansionInspection()));
    }

    public void testFaultyExpression() throws Exception {
        doTest("evaluateExpansionInspection/faultyExpression", withOnTheFly(new EvaluateExpansionInspection()));
    }
}
