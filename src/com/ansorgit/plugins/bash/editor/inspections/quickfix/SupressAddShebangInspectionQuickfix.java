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
