package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.util.HeredocSharedImpl;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Heredoc marker reference.
 */
abstract class HeredocMarkerReference extends CachingReference implements BindablePsiReference {
    protected final BashHereDocMarker marker;

    public HeredocMarkerReference(BashHereDocMarker marker) {
        this.marker = marker;
    }

    @Nullable
    @Override
    public abstract PsiElement resolveInner();

    @Override
    public BashHereDocMarker getElement() {
        return marker;
    }

    @Override
    public TextRange getRangeInElement() {
        String markerText = marker.getText();

        return TextRange.create(HeredocSharedImpl.startMarkerTextOffset(markerText), HeredocSharedImpl.endMarkerTextOffset(markerText));
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return marker.getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newElementName)) {
            throw new IncorrectOperationException("Invalid name");
        }

        return BashPsiUtils.replaceElement(marker, createMarkerElement(newElementName));
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (element instanceof BashHereDocMarker) {
            return handleElementRename(((BashHereDocMarker) element).getMarkerText());
        }

        throw new IncorrectOperationException("Unsupported element type");
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

    protected abstract PsiElement createMarkerElement(String name);
}
