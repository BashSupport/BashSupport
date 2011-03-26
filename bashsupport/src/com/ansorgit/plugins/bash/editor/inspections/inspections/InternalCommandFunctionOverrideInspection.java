/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ConvertSubshellInspection.java, Class: ConvertSubshellInspection
 * Last modified: 2009-12-04
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

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects function names which override internal bash commands.
 */
public class InternalCommandFunctionOverrideInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "InternalCommandFunctionOverride";
    }

    @NotNull
    public String getShortName() {
        return "Function overrides internal command";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Function overrides internal command";
    }

    @Override
    public String getStaticDescription() {
        return "Detects function definitions which override built-in Bash commands.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFunctionDef(BashFunctionDef functionDef) {
                if (LanguageBuiltins.isInternalCommand(functionDef.getName())) {
                    PsiElement targetElement = functionDef.getNameSymbol();
                    if (targetElement == null) {
                        targetElement = functionDef.getNavigationElement();
                    }

                    holder.registerProblem(targetElement, "Function overrides internal Bash command", ProblemHighlightType.GENERIC_ERROR_OR_WARNING);
                }
            }
        };
    }
}