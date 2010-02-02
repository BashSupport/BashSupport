/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: RecursiveIncludeFileInspection.java, Class: RecursiveIncludeFileInspection
 * Last modified: 2010-01-25
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
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * User: jansorg
 * Date: Nov 2, 2009
 * Time: 8:15:59 PM
 */
public class RecursiveIncludeFileInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "RecursiveInclusion";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Recursive file inclusion";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Recursive file inclusion";
    }

    @Override
    public String getStaticDescription() {
        return "Checks for recursive file inclusion. Currently it can highlight the inclusion of a file in itself..";
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
            public void visitInternalCommand(BashCommand bashCommand) {
                if (".".equals(bashCommand.getReferencedName())) {
                    List<BashPsiElement> params = bashCommand.parameters();
                    if (params.size() == 1) {
                        BashPsiElement firstParam = params.get(0);
                        if (firstParam instanceof BashCharSequence) {
                            String filename = ((BashCharSequence) firstParam).getUnwrappedCharSequence();
                            PsiFile containingFile = bashCommand.getContainingFile();
                            PsiFile file = BashPsiFileUtils.findRelativeFile(containingFile, filename);

                            if (file != null && file.equals(containingFile)) {
                                holder.registerProblem(firstParam, "The included file '" + filename + "' also includes this file.");
                            }
                        }
                    }
                }
            }
        };
    }
}