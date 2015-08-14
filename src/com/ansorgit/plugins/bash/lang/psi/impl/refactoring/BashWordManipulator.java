package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Manipulator for BashWord PSI elements.
 * Needed to compute the value range, which is used by the language injection feature of IntelliJ.
 *
 * @author jansorg
 */
public class BashWordManipulator implements ElementManipulator<BashWord> {
    @Override
    public BashWord handleContentChange(@NotNull BashWord element, @NotNull TextRange textRange, String newContent) throws IncorrectOperationException {
        PsiElement newElement = BashPsiElementFactory.createWord(element.getProject(), newContent);
        assert newElement instanceof BashWord;

        return BashPsiUtils.replaceElement(element, newElement);
    }

    @Override
    public BashWord handleContentChange(@NotNull BashWord element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, getRangeInElement(element), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashWord element) {
        return element.getTextContentRange();
    }
}
