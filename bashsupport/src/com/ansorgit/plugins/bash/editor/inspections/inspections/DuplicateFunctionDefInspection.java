/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: DuplicateFunctionDefInspection.java, Class: DuplicateFunctionDefInspection
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
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashFunctionProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection highlights duplicate function definitions.
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 8:25:43 PM
 */
public class DuplicateFunctionDefInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "DuplicateFunction";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Duplicate function";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Duplicate function definition";
    }

    @Override
    public String getStaticDescription() {
        return "Detects duplicate function definitions and highlights the double definitions. There is a chance of false" +
                "positives if the earlier definition is inside of a conditional command.";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFunctionDef(BashFunctionDef functionDef) {
                PsiElement start = functionDef.getPrevSibling() != null
                        ? functionDef.getPrevSibling()
                        : functionDef.getContext();

                BashFunctionProcessor p = new BashFunctionProcessor(functionDef.getName());

                BashResolveUtil.walkThrough(p, start, functionDef.getContext(), functionDef, true, true);
                PsiElement result = p.hasResults() ? p.getBestResult(true, functionDef) : null;
                if (result != null) {
                    String message = "The function '" + functionDef.getName() +
                            "' is already defined on line " + BashPsiUtils.getElementLineNumber(result) + ".";
                    holder.registerProblem(
                            functionDef.getNameSymbol(),
                            message,
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                    );
                }
            }
        };
    }
}
