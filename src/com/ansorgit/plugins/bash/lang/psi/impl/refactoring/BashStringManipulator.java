package com.ansorgit.plugins.bash.lang.psi.impl.refactoring;

import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashStringUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator for String elenents.
 *
 * @author jansorg
 */
public class BashStringManipulator implements ElementManipulator<BashString> {
    @Override
    public BashString handleContentChange(@NotNull BashString element, @NotNull TextRange textRange, String contentForRange) throws IncorrectOperationException {
        String oldContent = element.getText();
        String escapedContent = BashStringUtils.escape(contentForRange, '"');

        String newContent = textRange.replace(oldContent, escapedContent);

        PsiElement replacement = BashPsiElementFactory.createString(element.getProject(), newContent);
        assert replacement instanceof BashString;

        return BashPsiUtils.replaceElement(element, replacement);
    }

    @Override
    public BashString handleContentChange(@NotNull BashString element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, TextRange.create(0, element.getTextLength()), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull BashString element) {
        return element.getTextContentRange();
    }
}
