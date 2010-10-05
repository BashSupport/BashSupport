/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AddShebangInspection.java, Class: AddShebangInspection
 * Last modified: 2010-06-30
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

import com.ansorgit.plugins.bash.editor.inspections.quickfix.AddShebangQuickfix;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

/**
 * This inspection detects a missing shebang line and offers a file-level quickfix to add one.
 * Date: 15.05.2009
 * Time: 14:56:55
 *
 * @author Joachim Ansorg
 */
public class AddShebangInspection extends AbstractBashInspection {

    @Pattern("[a-zA-Z_0-9.]+")
    @NotNull
    @Override
    public String getID() {
        return "AddShebangLine";
    }

    @NotNull
    public String getShortName() {
        return "Add Shebang line";
    }

    @Nls
    @NotNull
    public String getDisplayName() {
        return "Add missing shebang line to file";
    }

    @Override
    public String getStaticDescription() {
        return "If a file does not yet have a shebang line this inspection offers " +
                "a file wide quickfix to add one.";
    }

    @Override
    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull InspectionManager manager, boolean isOnTheFly) {
        if (file instanceof BashFile) {
            BashFile bashFile = (BashFile) file;

            Boolean isLanguageConsole = file.getUserData(BashFile.LANGUAGE_CONSOLE_MARKER);

            if ((isLanguageConsole == null || !isLanguageConsole) && !bashFile.hasShebangLine()) {
                return new ProblemDescriptor[]{
                        manager.createProblemDescriptor(file, getShortName(), new AddShebangQuickfix(), ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly)
                };
            }
        }

        return null;
    }

    @NotNull
    @Override
    public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
        return new BashVisitor() {
            @Override
            public void visitFile(BashFile file) {
                addDescriptors(checkFile(file, holder.getManager(), isOnTheFly));
            }

            private void addDescriptors(final ProblemDescriptor[] descriptors) {
                if (descriptors != null) {
                    for (ProblemDescriptor descriptor : descriptors) {
                        holder.registerProblem(descriptor);
                    }
                }
            }
        };
    }
}