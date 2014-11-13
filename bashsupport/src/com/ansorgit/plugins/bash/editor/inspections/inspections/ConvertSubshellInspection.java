/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ConvertSubshellInspection.java, Class: ConvertSubshellInspection
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.SubshellQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
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
public class ConvertSubshellInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "ReplaceWithBackquote";
    }

    @NotNull
    public String getShortName() {
        return "Replace with backquote";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Convert subshell to backquote command";
    }

    @Override
    public String getStaticDescription() {
        return "Replaces a subshell expression with a backtick expression. For example: $(echo a) would be replaced by `echo a`.";
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitSubshell(BashSubshellCommand subshellCommand) {
                if (isOnTheFly) {
                    holder.registerProblem(subshellCommand, getShortName(), new SubshellQuickfix(subshellCommand));
                }
            }
        };
    }
}