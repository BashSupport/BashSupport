package com.ansorgit.plugins.bash.editor.inspections.inspections;

public class EvaluateExpansionInspectionTest extends AbstractInspectionTestCase {
    public void testEvaluation() throws Exception {
        doTest("evaluateExpansionInspection/evaluation", EvaluateExpansionInspection.class, true);
    }

    public void testFaultyExpression() throws Exception {
        doTest("evaluateExpansionInspection/faultyExpression", EvaluateExpansionInspection.class, true);
    }
}
