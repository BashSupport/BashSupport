/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: UnregisterGlobalVarInspection.java, Class: UnregisterGlobalVarInspection
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.UnregisterGlobalVariableQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This inspection marks global variables and offers a unregister quickfix.
 * <p/>
 * Of course, there is a chance of false positives as both inclusions may be in conditional
 * statements.
 * <p/>
 * User: jansorg
 * Date: Jan 25, 2010
 * Time: 10:11:49 PM
 */
public class UnregisterGlobalVarInspection extends AbstractBashInspection {
    //private static final Logger log = Logger.getInstance("#UnresolvedVariable");

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unregister as a global variable";
    }

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "unregisterGlobalVariableInspection";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Unregister global variable";
    }

    @Override
    public String getStaticDescription() {
        return "Unknown variables can be registered as global variables to remove the error highlighting. This inspection provides an unregister action for already registered variables.";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new UnresolvedVarVisitor(holder);
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.ERROR;
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