/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: ReadonlyVariableInspection.java, Class: ReadonlyVariableInspection
 * Last modified: 2013-04-30
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection marks unresolved variables.
 * <p/>
 *
 * @author jansorg
 */
public class ReadonlyVariableInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarDef(BashVarDef varDef) {
                if (varDef instanceof BashVar) {
                    BashVar var = (BashVar) varDef;
                    PsiElement resolve = var.getReference().resolve();

                    if (resolve != varDef && resolve instanceof BashVarDef) {
                        BashVarDef originalDefinition = (BashVarDef) resolve;

                        if (originalDefinition.isReadonly() && varDef.hasAssignmentValue()) {
                            holder.registerProblem(varDef, "Change to a readonly variable", LocalQuickFix.EMPTY_ARRAY);
                        }
                    }
                }
            }
        };
    }
}