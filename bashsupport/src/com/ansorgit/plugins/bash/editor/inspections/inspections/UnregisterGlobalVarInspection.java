/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: UnregisterGlobalVarInspection.java, Class: UnregisterGlobalVarInspection
 * Last modified: 2013-05-09
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.UnregisterGlobalVariableQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This inspection marks global variables and offers a unregister quickfix.
 * <p/>
 * Of course, there is a chance of false positives as both inclusions may be in conditional
 * statements.
 */
public class UnregisterGlobalVarInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new UnresolvedVarVisitor(holder);
    }

    private static class UnresolvedVarVisitor extends BashVisitor {
        private final ProblemsHolder holder;
        private final Set<String> globalVars;

        public UnresolvedVarVisitor(ProblemsHolder holder) {
            this.holder = holder;
            this.globalVars = BashProjectSettings.storedSettings(holder.getProject()).getGlobalVariables();
        }

        @Override
        public void visitVarUse(BashVar bashVar) {
            BashReference ref = bashVar.getReference();
            if (!bashVar.isBuiltinVar() && ref.resolve() == null) {
                String varName = ref.getReferencedName();

                boolean isRegisteredAsGlobal = globalVars.contains(varName);

                if (isRegisteredAsGlobal) {
                    holder.registerProblem(bashVar, "This variable is currently registered as a global variable",
                            ProblemHighlightType.INFO,
                            ref.getRangeInElement(),
                            new UnregisterGlobalVariableQuickfix(bashVar));
                }
            }
        }
    }
}