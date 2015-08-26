package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator implementation for HereDoc content elements.
 *
 * @author jansorg
 */
public class BashHereDocManipulator implements ElementManipulator<BashHereDoc> {
    @Override
    public BashHereDoc handleContentChange(@NotNull BashHereDoc bashHereDoc, @NotNull TextRange textRange, String newContent) throws IncorrectOperationException {
        PsiElement replacement = BashPsiElementFactory.createHeredocContent(bashHereDoc.getProject(), newContent);

        return BashPsiUtils.replaceElement(bashHereDoc, replacement);
    }

    @Override
    public BashHereDoc handleContentChange(@NotNull BashHereDoc element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashHereDoc element) {
        return TextRange.create(0, element.getTextLength());
    }
}
