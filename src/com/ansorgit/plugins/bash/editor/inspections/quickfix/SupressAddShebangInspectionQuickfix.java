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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.editor.inspections.BashInspections;
import com.ansorgit.plugins.bash.editor.inspections.SupressionUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.SuppressQuickFix;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

public class SupressAddShebangInspectionQuickfix implements SuppressQuickFix {
    private final String inspectionId;

    public SupressAddShebangInspectionQuickfix(String inspectionId) {
        this.inspectionId = inspectionId;
    }

    // @Override // no override to be compatible with earlier versions
    public boolean isSuppressAll() {
        return false;
    }

    @NotNull
    @Override
    public String getName() {
        return "Suppress for file ...";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return BashInspections.FAMILY_NAME;
    }

    @Override
    public boolean isAvailable(@NotNull Project project, @NotNull PsiElement context) {
        return true;
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        PsiFile file = descriptor.getPsiElement().getContainingFile();
        if (file == null) {
            return;
        }

        if (!FileModificationService.getInstance().preparePsiElementForWrite(file)) {
            return;
        }

        PsiComment suppressionComment = SupressionUtil.createSuppressionComment(project, inspectionId);

        PsiElement firstChild = file.getFirstChild();
        PsiElement inserted;
        if (firstChild != null) {
            inserted = file.addBefore(suppressionComment, firstChild);
        } else {
            inserted = file.add(suppressionComment);
        }

        if (inserted != null) {
            file.addAfter(BashPsiElementFactory.createNewline(project), inserted);
        }

        UndoUtil.markPsiFileForUndo(file);
    }
}
