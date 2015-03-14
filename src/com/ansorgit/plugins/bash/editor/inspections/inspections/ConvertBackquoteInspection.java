/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ConvertBackquoteInspection.java, Class: ConvertBackquoteInspection
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.BackquoteQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection can convert the backquote commands into subshell commands.
 * <p/>
 * User: jansorg
 * Date: 21.05.2009
 * Time: 13:49:05
 */
public class ConvertBackquoteInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "ReplaceWithSubshell";
    }

    @NotNull
    public String getShortName() {
        return "Replace with subshell";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Convert backquote to subshell commands";
    }

    @Override
    public String getStaticDescription() {
        return "Replaces a backtick command with a corresponding subshell expression. For example: `echo a` would be replaced by $(echo a).";
    }

    @NotNull
    @Override
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.WARNING;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder problemsHolder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitBackquoteCommand(BashBackquote backquote) {
                if (isOnTheFly) {
                    problemsHolder.registerProblem(backquote, getShortName(), new BackquoteQuickfix(backquote));
                }
            }
        };
    }
}
