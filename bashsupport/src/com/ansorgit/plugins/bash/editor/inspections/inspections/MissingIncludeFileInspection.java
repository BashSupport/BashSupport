/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: MissingIncludeFileInspection.java, Class: MissingIncludeFileInspection
 * Last modified: 2010-05-11
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
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * User: jansorg
 * Date: Nov 2, 2009
 * Time: 8:15:59 PM
 */
public class MissingIncludeFileInspection extends AbstractBashInspection {
    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "MissingInclude";
    }

    @NotNull
    @Override
    public String getShortName() {
        return "Missing include file";
    }

    @Nls
    @NotNull
    @Override
    public String getDisplayName() {
        return "Missing include file";
    }

    @Override
    public String getStaticDescription() {
        return "Checks the filenames of include directives. If a given file doesn't exist then" +
                "the element is highlighted as an error. Includes of files given as runtime values (e.g. variables) are not evaluated.";
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
            public void visitIncludeCommand(BashIncludeCommand bashCommand) {
                //fixme support $PATH evaluation

                BashFileReference fileReference = bashCommand.getFileReference();
                PsiFile file = fileReference.findReferencedFile();
                if (file == null && fileReference.isStatic()) {
                    String filename = fileReference.getFilename();

                    //check if it's an existing absolute file
                    File diskFile = new File(filename);
                    boolean absoluteAndExists = diskFile.isAbsolute() && diskFile.exists();
                    if (!absoluteAndExists) {
                        holder.registerProblem(fileReference, "The file '" + filename + "' does not exist.");
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
