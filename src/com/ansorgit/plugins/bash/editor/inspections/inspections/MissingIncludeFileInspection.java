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
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Detects include file statements which reference missing files.
 * @author jansorg
 */
public class MissingIncludeFileInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitIncludeCommand(BashIncludeCommand bashCommand) {
                //fixme support $PATH evaluation

                BashFileReference fileReference = bashCommand.getFileReference();
                if (fileReference == null) {
                    return;
                }

                PsiFile file = fileReference.findReferencedFile();
                if (file == null && fileReference.isStatic()) {
                    String filename = fileReference.getFilename();

                    //check if it's an existing absolute file
                    File diskFile = new File(filename);
                    boolean absoluteAndExists = diskFile.isAbsolute() && diskFile.exists();
                    if (!absoluteAndExists) {
                        holder.registerProblem(fileReference, String.format("The file '%s' does not exist.", filename));
                    }

                    //print an error message if the given path is a directory
                    if (absoluteAndExists && diskFile.isDirectory()) {
                        holder.registerProblem(fileReference, "Unable to include a directory.");
                    }
                }
            }
        };
    }
}
