package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.editor.inspections.InspectionProvider;
import com.ansorgit.plugins.bash.editor.inspections.SupressionUtil;
import com.ansorgit.plugins.bash.editor.inspections.inspections.AddShebangInspection;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
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
    public SupressAddShebangInspectionQuickfix() {
        super();
    }

    @NotNull
    @Override
    public String getName() {
        return "Suppress for file ...";
    }

    @NotNull
    @Override
    public String getFamilyName() {
        return InspectionProvider.BASH_FAMILY;
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

        PsiComment suppressionComment = SupressionUtil.createSuppressionComment(project, AddShebangInspection.ID);

        PsiElement firstChild = file.getFirstChild();
        PsiElement inserted;
        if (firstChild != null) {
            inserted = file.addBefore(suppressionComment, firstChild);
        } else {
            inserted = file.add(suppressionComment);
        }

        if (inserted != null) {
            file.addAfter(BashChangeUtil.createNewline(project), inserted);
        }

        UndoUtil.markPsiFileForUndo(file);
    }
}
