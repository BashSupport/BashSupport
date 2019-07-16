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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection marks unresolved variables.
 * <br>
 *
 * @author jansorg
 */
public class ReadonlyVariableInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarDef(final BashVarDef visitedVarDef) {
                if (visitedVarDef.isReadonly() || !visitedVarDef.hasAssignmentValue()) {
                    //if directly declared as read-only then an assignment is not seen as modification, e.g. in "declare -r a=b"
                    return;
                }

                String name = visitedVarDef.getName();
                if (name == null) {
                    return;
                }

                BashResolveUtil.walkVariableDefinitions(visitedVarDef, varDef -> {
                    //check if the found read-only definition is a definition for the visited definition
                    if (varDef != visitedVarDef
                            && !visitedVarDef.isEquivalentTo(varDef)
                            && varDef != null
                            && varDef.isReadonly()
                            && BashPsiUtils.isValidReferenceScope(visitedVarDef, varDef)) {

                        registerWarning(visitedVarDef, holder);
                        return false;
                    }

                    return true;
                });
            }
        };
    }

    private void registerWarning(BashVarDef varDef, @NotNull ProblemsHolder holder) {
        holder.registerProblem(varDef, "Change to a read-only variable", LocalQuickFix.EMPTY_ARRAY);
    }
}