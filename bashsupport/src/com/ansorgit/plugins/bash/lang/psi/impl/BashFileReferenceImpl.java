package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashFileReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiFileUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 19:59
 */
public class BashFileReferenceImpl extends BashPsiElementImpl implements BashFileReference {
    public BashFileReferenceImpl(final ASTNode astNode) {
        super(astNode, "File reference");
    }

    @NotNull
    public String getFilename() {
        PsiElement firstParam = getFirstChild();
        if (firstParam instanceof BashCharSequence) {
            return ((BashCharSequence) firstParam).getUnwrappedCharSequence();
        }

        return getText();
    }

    public boolean isStatic() {
        PsiElement firstChild = getFirstChild();
        return firstChild instanceof BashCharSequence && ((BashCharSequence) firstChild).isStatic();
    }

    @Nullable
    public PsiFile findReferencedFile() {
        PsiFile containingFile = getContainingFile();
        return BashPsiFileUtils.findRelativeFile(containingFile, getFilename());
    }

    public PsiElement getElement() {
        return this;
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFileReference(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public TextRange getRangeInElement() {
        PsiElement firstChild = getFirstChild();

        if (firstChild instanceof BashCharSequence) {
            return ((BashCharSequence) firstChild).getTextContentRange();
        }

        return TextRange.from(0, getTextLength());
    }

    public PsiElement resolve() {
        return findReferencedFile();
    }

    @NotNull
    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        return BashPsiUtils.replaceElement(this, BashChangeUtil.createWord(getProject(), newName));
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException("not supported");
    }

    public boolean isReferenceTo(PsiElement element) {
        return element == this || element.equals(findReferencedFile());
    }

    @NotNull
    public Object[] getVariants() {
        return PsiElement.EMPTY_ARRAY;
    }

    public boolean isSoft() {
        return false;
    }
}
