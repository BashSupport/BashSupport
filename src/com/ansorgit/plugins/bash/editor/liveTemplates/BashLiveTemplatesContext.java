package com.ansorgit.plugins.bash.editor.liveTemplates;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.Language;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a live template context for Bash files.
 */
public class BashLiveTemplatesContext extends TemplateContextType {
    protected BashLiveTemplatesContext() {
        super("Bash", "Bash");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        Language language = PsiUtilCore.getLanguageAtOffset(file, offset);
        if (language.isKindOf(BashFileType.BASH_LANGUAGE)) {
            PsiElement element = file.findElementAt(offset);
            if (element == null) {
                //if a user edits at the end of a comment at the end of a file then findElementAt returns null
                //(for yet unknown reasons)
                element = file.findElementAt(offset - 1);
            }

            return PsiTreeUtil.getParentOfType(element, PsiComment.class, false) == null
                    && PsiTreeUtil.getParentOfType(element, BashShebang.class, false) == null;
        }

        return false;
    }
}
