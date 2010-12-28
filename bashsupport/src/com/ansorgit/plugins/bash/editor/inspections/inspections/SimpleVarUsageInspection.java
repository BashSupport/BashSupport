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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.ReplaceVarWithParamExpansionQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects simple variable usages and offers a quickfix to replace it with
 * the equivalent parameter expansion, e.g. $a would be replaced with ${a}.
 * <p/>
 * User: jansorg
 * Date: 21.05.2009
 * Time: 13:49:05
 */
public class SimpleVarUsageInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "SimpleVarUsage";
    }

    @NotNull
    public String getShortName() {
        return "Simple variable usage";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Replace with the equivalent parameter expansion";
    }

    @Override
    public String getStaticDescription() {
        return "Replaces the simple use of a variable with the equivalent parameter expansion form. For example $a is replaced by ${a}.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.INFO;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitVarUse(BashVar var) {
                if (!var.isParameterExpansion() && !var.isBuiltinVar()) {
                    holder.registerProblem(var, getShortName(), new ReplaceVarWithParamExpansionQuickfix(var));
                }
            }
        };
    }
}