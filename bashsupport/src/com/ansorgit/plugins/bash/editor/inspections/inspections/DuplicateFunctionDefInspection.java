/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: DuplicateFunctionDefInspection.java, Class: DuplicateFunctionDefInspection
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
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashFunctionProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * This inspection highlights duplicate function definitions.
 */
public class DuplicateFunctionDefInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFunctionDef(BashFunctionDef functionDef) {
                BashFunctionProcessor p = new BashFunctionProcessor(functionDef.getName(), true);

                boolean isOnGlobalLevel = BashPsiUtils.findNextVarDefFunctionDefScope(functionDef) == null;
                PsiElement start = functionDef.getContext() != null && !isOnGlobalLevel
                        ? functionDef.getContext()
                        : functionDef.getPrevSibling();

                if (start != null) {
                    PsiTreeUtil.treeWalkUp(p, start, functionDef.getContainingFile(), ResolveState.initial());

                    if (p.hasResults()) {
                        List<PsiElement> results = p.getResults() != null ? Lists.newArrayList(p.getResults()) : Lists.<PsiElement>newArrayList();
                        results.remove(functionDef);

                        if (results.size() > 0) {
                            //find the result which has the lowest textOffset in the file
                            PsiElement firstFunctionDef = results.get(0);
                            for (PsiElement e : results) {
                                if (e.getTextOffset() < firstFunctionDef.getTextOffset()) {
                                    firstFunctionDef = e;
                                }
                            }

                            if (firstFunctionDef.getTextOffset() < functionDef.getTextOffset()) {
                                BashFunctionDefName nameSymbol = functionDef.getNameSymbol();

                                if (nameSymbol != null) {
                                    String message = String.format("The function '%s' is already defined at line %d.",
                                            functionDef.getName(),
                                            BashPsiUtils.getElementLineNumber(firstFunctionDef));

                                    holder.registerProblem(
                                            nameSymbol,
                                            message,
                                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING
                                    );
                                }
                            }
                        }
                    }
                }
            }
        };
    }
}
