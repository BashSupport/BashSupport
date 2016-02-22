package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract variable reference implementation to allow different implementations for smart and dumb mode.
 *
 * @author jansorg
 */
abstract class AbstractBashVarReference extends CachingReference implements BashReference, BindablePsiReference {
    protected final BashVarImpl bashVar;

    public AbstractBashVarReference(BashVarImpl bashVar) {
        this.bashVar = bashVar;
    }

    @Override
    public PsiElement getElement() {
        return bashVar;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);
    }

    @Override
    public TextRange getRangeInElement() {
        return bashVar.getNameTextRange();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return bashVar.getReferenceName();
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newName)) {
            throw new IncorrectOperationException("Can't have an empty name");
        }

        //if this is variable which doesn't have a $ or escaped \$ sign prefix
        if (bashVar.getPrefixLength() == 0) {
            return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, true));
        }

        return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, false));
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return handleElementRename(element.getText());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

    @Override
    public String getReferencedName() {
        return bashVar.getReferenceName();
    }
}
