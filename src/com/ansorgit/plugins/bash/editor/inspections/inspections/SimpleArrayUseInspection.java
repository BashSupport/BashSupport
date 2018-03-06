/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects use of array variables without array element qualifiers.
 */
public class SimpleArrayUseInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarUse(BashVar var) {
                BashVarDef definition = (BashVarDef) var.getReference().resolve();
                if (definition != null) {
                    boolean defIsArray = definition.isArray();
                    boolean arrayUse = var.isArrayUse();

                    if (arrayUse && !defIsArray) {
                        holder.registerProblem(var, "Array use of non-array variable", ProblemHighlightType.WEAK_WARNING);
                    } else if (!arrayUse
                            && defIsArray
                            && !BashPsiUtils.hasParentOfType(var, BashString.class, 5)
                            && !BashPsiUtils.hasParentOfType(var, ArithmeticExpression.class, 5)) {

                        holder.registerProblem(var, "Simple use of array variable", ProblemHighlightType.WEAK_WARNING);
                    }
                }
            }
        };
    }
}