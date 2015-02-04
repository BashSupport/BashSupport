/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FloatArithmeticInspection.java, Class: FloatArithmeticInspection
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

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ProductExpression;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects floating point arithmetic using Bash arithmetic (so it probably does not produce
 * the intended results).
 * <p/>
 *
 * @author Joachim Ansorg
 */
public class FloatArithmeticInspection extends AbstractBashInspection {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitArithmeticExpression(ArithmeticExpression expression) {
                if (!expression.isStatic()) {
                    return;
                }

                if (expression instanceof ProductExpression) {
                    ProductExpression product = (ProductExpression) expression;

                    if (product.hasDivisionRemainder()) {
                        holder.registerProblem(expression, "Integer division with remainder found.");
                    }
                }
            }
        };
    }
}