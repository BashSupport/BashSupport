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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashFiles;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Detects include file statements which reference missing files.
 *
 * @author jansorg
 */
public class MissingIncludeFileInspection extends LocalInspectionTool {
    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, boolean isOnTheFly) {
        return new IncludeFileVisitor(holder);
    }

    private static boolean containsUnsupportedVars(PsiElement fileReference) {
        AtomicBoolean otherVars = new AtomicBoolean(false);
        BashPsiUtils.visitRecursively(fileReference, new BashVisitor() {
            @Override
            public void visitVarUse(BashVar var) {
                if (!"HOME".equals(var.getReferenceName())) {
                    otherVars.set(true);
                }
            }
        });

        return otherVars.get();
    }

    private static class IncludeFileVisitor extends BashVisitor {
        private final ProblemsHolder holder;

        public IncludeFileVisitor(ProblemsHolder holder) {
            this.holder = holder;
        }

        @Override
        public void visitIncludeCommand(BashIncludeCommand bashCommand) {
            BashFileReference fileReference = bashCommand.getFileReference();
            if (fileReference == null || fileReference.findReferencedFile() != null) {
                return;
            }

            String filename = fileReference.getFilename();
            if (fileReference.isDynamic() || BashFiles.containsSupportedPlaceholders(filename)) {
                if (!BashProjectSettings.storedSettings(holder.getProject()).isValidateWithCurrentEnv()) {
                    // don't validate with current environment variables -> quit early if the filename contains variables
                    return;
                }

                // we can't handle other vars than $HOME for now.
                if (containsUnsupportedVars(fileReference)) {
                    return;
                }
            }

            filename = BashFiles.replaceHomePlaceholders(filename);

            //check if it's an existing absolute file
            try {
                Path path = Paths.get(filename);
                boolean absoluteAndExists = path.isAbsolute() && Files.exists(path);

                if (!absoluteAndExists) {
                    holder.registerProblem(fileReference, String.format("The file '%s' does not exist.", filename));
                } else if (Files.isDirectory(path)) {
                    //print an error message if the given path is a directory
                    holder.registerProblem(fileReference, "Unable to include a directory.");
                }
            } catch (InvalidPathException e) {
                holder.registerProblem(fileReference, String.format("Unable to parse file reference '%s'", filename));
            }
        }
    }
}
