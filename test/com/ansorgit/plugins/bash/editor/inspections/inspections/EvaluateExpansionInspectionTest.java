package com.ansorgit.plugins.bash.editor.inspections.inspections;

import org.junit.Test;

public class EvaluateExpansionInspectionTest extends AbstractInspectionTestCase {
    @Test
    public void testEvaluation() throws Exception {
        doTest("evaluateExpansionInspection/evaluation", EvaluateExpansionInspection.class, true);
    }

    @Test
    public void testFaultyExpression() throws Exception {
        doTest("evaluateExpansionInspection/faultyExpression", EvaluateExpansionInspection.class, true);
    }
}
