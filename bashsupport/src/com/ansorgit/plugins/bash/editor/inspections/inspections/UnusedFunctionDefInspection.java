/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: UnusedFunctionDefInspection.java, Class: UnusedFunctionDefInspection
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashSymbol;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
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
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 9:19:56 PM
 */
public class UnusedFunctionDefInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "UnusedFunction";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Unused function definition";
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
                BashSymbol nameSymbol = functionDef.getNameSymbol();

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
