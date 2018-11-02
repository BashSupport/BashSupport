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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.DoubleBracketsQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashConditionalCommand;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Detects single brackets and recommends to use double brackets
 * @author dheid
 */
public class UseExtendedTestCommandInspection extends LocalInspectionTool {

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new ExtendedTestCommandVisitor(isOnTheFly, holder);
    }

    static class ExtendedTestCommandVisitor extends BashVisitor {
        private static final String DESCRIPTION = "Replace with double brackets";

        private final boolean onTheFly;
        private final ProblemsHolder holder;

        ExtendedTestCommandVisitor(boolean onTheFly, ProblemsHolder holder) {
            this.onTheFly = onTheFly;
            this.holder = holder;
        }

        @Override
        public void visitConditional(BashConditionalCommand conditionalCommand) {
            if (onTheFly) {
                DoubleBracketsQuickfix quickfix = new DoubleBracketsQuickfix(conditionalCommand);
                holder.registerProblem(conditionalCommand, DESCRIPTION, quickfix);
            }
        }
    }
}