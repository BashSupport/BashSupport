/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: EvaluateArithmeticExpressionInspection.java, Class: EvaluateArithmeticExpressionInspection
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.editor.inspections.quickfix.EvaluateArithExprQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ParenthesesExpression;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Evaluate a static arithmetic expression. Offers a quickfix to insert the replacement value.
 * <p/>
 * User: jansorg
 * Date: Nov 15, 2009
 * Time: 12:48:24 AM
 */
public class EvaluateArithmeticExpressionInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "EvaluateArithmeticExpression";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Evaluate arithmetic expression";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Evaluate an arithmetic expression";
    }

    @Override
    public String getStaticDescription() {
        return "Replaces a static arithmetic expression with the result. For example the expression 1 + 3 * 4 would be replaced with the evaluated result of 13.";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitArithmeticExpression(ArithmeticExpression expression) {
                if (!isOnTheFly) {
                    return;
                }

                boolean isParenthesisExpr = expression instanceof ParenthesesExpression;

                List<ArithmeticExpression> subexpressions = expression.subexpressions();
                if (subexpressions.size() > 1 && (expression.isStatic() || isParenthesisExpr)) {
                    ArithmeticExpression parent = expression.findParentExpression();

                    //run only if the parent is not a static expression itself
                    if (parent == null || !parent.isStatic()) {
                        try {
                            String template = "Replace '" + expression.getText() + "' with the evaluated result of '" + expression.computeNumericValue() + "'";
                            holder.registerProblem(expression, template, new EvaluateArithExprQuickfix(expression));
                        } catch (Exception e) {
                            //fixme
                        }
                    }
                }
            }
        };
    }
}