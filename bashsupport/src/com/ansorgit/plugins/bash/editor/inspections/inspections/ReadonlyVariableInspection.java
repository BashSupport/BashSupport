/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ReadonlyVariableInspection.java, Class: ReadonlyVariableInspection
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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection marks unresolved variables.
 * <p/>
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 10:11:49 PM
 */
public class ReadonlyVariableInspection extends AbstractBashInspection {
    //private static final Logger log = Logger.getInstance("#UnresolvedVariable");

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Readonly variable";
    }

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "readonlyVariableInspection";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Readonly variable";
    }

    @Override
    public String getStaticDescription() {
        return "Attempt to change a read-only variable. Read-only variables can not be modified once they are declared.";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarDef(BashVarDef varDef) {
                if (varDef instanceof BashVar) {
                    BashVar var = (BashVar) varDef;
                    PsiElement resolve = var.resolve();

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

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
    }
}