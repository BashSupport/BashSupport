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
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

/**
 * Enforces function names to only contain lower case letters, underscores or double colon
 */
public class FunctionNamesShouldBeLowerSnakeCaseInspection extends LocalInspectionTool {

    private static class FunctionNameVisitor extends BashVisitor {
        private static final Pattern LOWER_SNAKE_CASE = Pattern.compile("([a-z_0-9]|::)+");
        private final ProblemsHolder holder;

        FunctionNameVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitFunctionDef(BashFunctionDef functionDef) {
            String functionName = functionDef.getName();
            if (functionName != null && doesNotMatchPattern(functionName)) {
                PsiElement targetElement = functionDef.getNameSymbol();
                if (targetElement == null) {
                    targetElement = functionDef.getNavigationElement();
                }
                holder.registerProblem(targetElement, "Function name must fit lower snake case", ProblemHighlightType.WEAK_WARNING);
            }
        }

        private static boolean doesNotMatchPattern(@NotNull CharSequence functionName) {
            return !LOWER_SNAKE_CASE.matcher(functionName).matches();
        }

    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new FunctionNameVisitor(holder);
    }

}