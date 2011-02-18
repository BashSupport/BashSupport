/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: RecursiveIncludeFileInspection.java, Class: RecursiveIncludeFileInspection
 * Last modified: 2010-03-24
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
import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This inspection detects recursive file inclusion.
 * It can detect whether another file actually back-includes this file.
 * <p/>
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
            public void visitIncludeCommand(BashIncludeCommand includeCommand) {
                BashFileReference fileReference = includeCommand.getFileReference();

                PsiFile referencedFile = fileReference.findReferencedFile();
                if (includeCommand.getContainingFile().equals(referencedFile)) {
                    holder.registerProblem(fileReference, "A file should not include itself.");
                } else if (referencedFile instanceof BashFile) {
                    //check for deep recursive inclusion
                    Set<PsiFile> includingFiles = FileInclusionManager.findIncludingFiles(includeCommand.getProject(), referencedFile);
                    if (includingFiles.contains(includeCommand.getContainingFile())) {
                        holder.registerProblem(fileReference, "Possible recursive inclusion");
                    }
                }
            }
        };
    }
}