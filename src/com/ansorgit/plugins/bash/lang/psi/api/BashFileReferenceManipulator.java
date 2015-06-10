package com.ansorgit.plugins.bash.lang.psi.api;

import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Element manipulator for Bash file references.
 *
 * @author jansorg
 */
public class BashFileReferenceManipulator implements ElementManipulator {
    @Override
    public PsiElement handleContentChange(@NotNull PsiElement element, @NotNull TextRange range, String newContent) throws IncorrectOperationException {
        assert element instanceof BashFileReference;
        assert range.equals(getRangeInElement(element));

        BashFileReference ref = (BashFileReference) element;
        PsiElement firstChild = element.getFirstChild();
        if (firstChild instanceof BashCharSequence && ((BashCharSequence) firstChild).isWrapped()) {
            return BashPsiUtils.replaceElement(firstChild, BashChangeUtil.createString(ref.getProject(), ((BashCharSequence) firstChild).createEquallyWrappedString(newContent)));
        }

        return BashPsiUtils.replaceElement(ref, BashChangeUtil.createWord(ref.getProject(), newContent));
    }

    @Override
    public PsiElement handleContentChange(@NotNull PsiElement element, String newContent) throws IncorrectOperationException {
        return handleContentChange(element, getRangeInElement(element), newContent);
    }

    @NotNull
    @Override
    public TextRange getRangeInElement(@NotNull PsiElement element) {
        assert element instanceof BashFileReference;

        PsiElement firstChild = element.getFirstChild();
        if (firstChild instanceof BashCharSequence) {
            return ((BashCharSequence) firstChild).getTextContentRange().shiftRight(firstChild.getStartOffsetInParent());
        }

        return TextRange.from(0, element.getTextLength());
    }
}
