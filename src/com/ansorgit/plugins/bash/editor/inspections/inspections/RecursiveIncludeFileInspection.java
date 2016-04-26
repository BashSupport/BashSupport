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
import com.ansorgit.plugins.bash.lang.psi.FileInclusionManager;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * This inspection detects recursive file inclusion.
 * It can detect whether another file actually back-includes this file.
 * <br>
 * @author jansorg #
 */
public class RecursiveIncludeFileInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitIncludeCommand(BashIncludeCommand includeCommand) {
                BashFileReference fileReference = includeCommand.getFileReference();
                if (fileReference == null) {
                    return;
                }

                PsiFile referencedFile = fileReference.findReferencedFile();
                if (includeCommand.getContainingFile().equals(referencedFile)) {
                    holder.registerProblem(fileReference, "A file should not include itself.");
                } else if (referencedFile instanceof BashFile) {
                    //check for deep recursive inclusion
                    //fixme
                    Set<PsiFile> includedFiles = FileInclusionManager.findIncludedFiles(referencedFile, true, true);
                    if (includedFiles.contains(includeCommand.getContainingFile())) {
                        holder.registerProblem(fileReference, "Possible recursive inclusion");
                    }
                }
            }
        };
    }
}