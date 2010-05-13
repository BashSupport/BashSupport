/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: EvaluateExpansionInspection.java, Class: EvaluateExpansionInspection
 * Last modified: 2010-05-13
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.EvaluateExpansionQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects expansions and offers a quickfix to calculate the expansion
 * and insert it instead of the original expansion placeholder.
 * <p/>
 * User: jansorg
 * Date: Nov 15, 2009
 * Time: 12:48:24 AM
 */
public class EvaluateExpansionInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "EvaluateExpression";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Evaluate expansion";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Evaluate an expansion";
    }

    @Override
    public String getStaticDescription() {
        return "Replaces a Bash expansion with the evaluated result. Only static value expansions are understood by this expansion, i.e. if an expansion contains a variable then the quickfix can not be applied.";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitExpansion(BashExpansion expansion) {
                if (isOnTheFly) {
                    holder.registerProblem(expansion, getShortName(), new EvaluateExpansionQuickfix(expansion, holder.getProject()));
                }
            }
        };
    }
}
