/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: UnusedFunctionDefInspection.java, Class: UnusedFunctionDefInspection
 * Last modified: 2013-01-25
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
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.ex.UnfairLocalInspectionTool;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Query;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * Highlights unused function definitions.
 * <p/>
 * fixme: This inspection currently does not work. For a proper implementation we'd need an index.
 */
public class UnusedFunctionDefInspection extends AbstractBashInspection implements UnfairLocalInspectionTool {

    public static final String SHORT_NAME = "Unused function definition";

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "UnusedFunction";
    }

    @NotNull
    @Override
    public String getShortName() {
        return SHORT_NAME;
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Unused function definition";
    }

    @Override
    public String getStaticDescription() {
        return "This inspection highlights function definitions which are never called in a Bash script.";
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFunctionDef(BashFunctionDef functionDef) {
                BashFunctionDefName nameSymbol = functionDef.getNameSymbol();

                if (nameSymbol != null) {
                    Query<PsiReference> search = ReferencesSearch.search(functionDef, functionDef.getUseScope(), true);
                    PsiReference first = search.findFirst();

                    if (first == null) {
                        holder.registerProblem(nameSymbol, getShortName(), ProblemHighlightType.LIKE_UNUSED_SYMBOL);
                    }
                }
            }
        };
    }
}
