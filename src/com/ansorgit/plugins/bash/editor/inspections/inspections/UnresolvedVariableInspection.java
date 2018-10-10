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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.GlobalVariableQuickfix;
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
 * This inspection marks unresolved variables.
 * <br>
 *
 * @author jansorg
 */
public class UnresolvedVariableInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new UnresolvedVariableVisitor(holder);
    }

    private static final class UnresolvedVariableVisitor extends BashVisitor {
        private final ProblemsHolder holder;
        private final Set<String> globalVariableNames;

        public UnresolvedVariableVisitor(ProblemsHolder holder) {
            this.holder = holder;
            globalVariableNames = BashProjectSettings.storedSettings(holder.getProject()).getGlobalVariables();
        }

        @Override
        public void visitVarUse(BashVar bashVar) {
            if (bashVar.isBuiltinVar() || bashVar.getTextLength() == 0) {
                return;
            }

            if (globalVariableNames.contains(bashVar.getReferenceName())) {
                return;
            }

            BashReference ref = bashVar.getReference();
            if (ref.resolve() == null) {
                holder.registerProblem(bashVar,
                        "Unresolved variable",
                        ProblemHighlightType.GENERIC_ERROR_OR_WARNING,
                        ref.getRangeInElement(),
                        new GlobalVariableQuickfix(bashVar, true));
            }
        }
    }
}
