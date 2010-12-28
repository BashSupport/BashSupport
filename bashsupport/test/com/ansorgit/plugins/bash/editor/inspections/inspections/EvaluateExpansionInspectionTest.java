package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 28.12.10
 * Time: 20:02
 */
public class EvaluateExpansionInspectionTest extends AbstractInspectionTestCase {
    protected EvaluateExpansionInspection tool = new EvaluateExpansionInspection() {
        @NotNull
        @Override
        public PsiElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly) {
            //override isOnTheFly because this inspection only works with isOnTheFly=true
            return super.buildVisitor(holder, true);
        }
    };

    public void testEvaluation() throws Exception {
        doTest("evaluateExpansionInspection/evaluation", tool);
    }

    public void testFaultyExpression() throws Exception {
        doTest("evaluateExpansionInspection/faultyExpression", tool);
    }
}
