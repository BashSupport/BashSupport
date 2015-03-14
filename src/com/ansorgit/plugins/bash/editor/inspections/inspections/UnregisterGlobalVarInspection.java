/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: UnregisterGlobalVarInspection.java, Class: UnregisterGlobalVarInspection
 * Last modified: 2010-03-24
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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

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
        return new BashVisitor() {
            @Override
            public void visitVarUse(BashVar bashVar) {
                if (!bashVar.isBuiltinVar() && bashVar.resolve() == null) {
                    String varName = bashVar.getReferencedName();

                    boolean isRegisteredAsGlobal = BashProjectSettings.storedSettings(bashVar.getProject()).getGlobalVariables().contains(varName);

                    if (isRegisteredAsGlobal) {
                        holder.registerProblem(bashVar, "This variable is currently registered as a global variable",
                                ProblemHighlightType.INFO,
                                bashVar.getRangeInElement(),
                                new UnregisterGlobalVariableQuickfix(bashVar));
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