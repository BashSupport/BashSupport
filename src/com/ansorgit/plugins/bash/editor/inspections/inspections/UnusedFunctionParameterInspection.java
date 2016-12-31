/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.loops.BashLoop;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Inspects function calls and checks whether the given parameters are actually used in the function definition.
 * <br>
 * This inspection is not capable to evaluate the control flow, e.g. parameter references in unreachable if
 * statements are still evaluated.
 * <br>
 * <br>
 *
 * @author jansorg
 */
public class UnusedFunctionParameterInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitGenericCommand(BashCommand bashCommand) {
                if (!bashCommand.isFunctionCall()) {
                    return;
                }

                PsiReference reference = bashCommand.getReference();
                if (reference == null) {
                    return;
                }

                BashFunctionDef functionDef = (BashFunctionDef) reference.resolve();
                if (functionDef == null) {
                    return;
                }

                //if "shift" is used inside of a loop in the function then proper detection is not possible
                if (isShiftUsedInLoop(functionDef)) {
                    return;
                }

                Set<String> referencedParameterNames = Sets.newHashSet(Lists.transform(functionDef.findReferencedParameters(), new Function<BashPsiElement, String>() {
                    public String apply(BashPsiElement element) {
                        if (element instanceof BashVar) {
                            return ((BashVar) element).getReference().getReferencedName();
                        }

                        return element.getText();
                    }
                }));

                //if the all parameter expansion feature is used consider all params as used
                if (referencedParameterNames.contains("*") || referencedParameterNames.contains("@")) {
                    return;
                }

                List<BashPsiElement> callerParameters = bashCommand.parameters();
                int callerParameterCount = callerParameters.size();

                for (int i = 0; i < callerParameterCount; i++) {
                    String paramName = String.valueOf(i + 1);

                    if (!referencedParameterNames.contains(paramName)) {
                        holder.registerProblem(callerParameters.get(i), "Unused function parameter", LocalQuickFix.EMPTY_ARRAY);
                    }
                }

            }

            boolean isShiftUsedInLoop(BashFunctionDef function) {
                AtomicBoolean shiftUsed = new AtomicBoolean(false);

                BashPsiUtils.visitRecursively(function, new BashVisitor() {
                    @Override
                    public void visitInternalCommand(BashCommand bashCommand) {
                        if (shiftUsed.get()) {
                            return;
                        }

                        if ("shift".equals(bashCommand.getReferencedCommandName())) {
                            BashLoop loop = BashPsiUtils.findParent(bashCommand, BashLoop.class);
                            if (loop != null && function.equals(BashPsiUtils.findParent(loop, BashFunctionDef.class, BashFunctionDef.class))) {
                                shiftUsed.set(true);
                            }
                        }
                    }
                });

                return shiftUsed.get();
            }
        };
    }

}
