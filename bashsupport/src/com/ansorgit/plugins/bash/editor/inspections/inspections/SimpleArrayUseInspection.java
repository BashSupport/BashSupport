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

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects use of array variables without array element qualifiers.
 */
public class SimpleArrayUseInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "SimpleArrayUse";
    }

    @NotNull
    public String getShortName() {
        return "Simple use of array variable";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Simple use of array variable";
    }

    @Override
    public String getStaticDescription() {
        return "Detects use of array variables without array element qualifiers.";
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
            public void visitVarUse(BashVar var) {
                if (!var.isArrayUse()) {
                    BashVarDef definition = (BashVarDef) var.resolve();
                    if (definition != null && definition.isArray()) {
                        holder.registerProblem(var, "Simple use of array variable", ProblemHighlightType.WEAK_WARNING);
                    }
                }
            }
        };
    }
}