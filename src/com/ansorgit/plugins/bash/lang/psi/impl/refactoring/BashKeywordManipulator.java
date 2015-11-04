package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
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
public class BashKeywordManipulator implements ElementManipulator<BashKeyword> {
    @Override
    public BashKeyword handleContentChange(@NotNull BashKeyword bashHereDoc, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @Override
    public BashKeyword handleContentChange(@NotNull BashKeyword element, String newContent) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashKeyword element) {
        PsiElement keywordElement = element.keywordElement();

        return TextRange.create(0, keywordElement.getTextLength());
    }
}
