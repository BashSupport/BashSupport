/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    HeredocMarkerReference(BashHereDocMarker marker) {
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

        return TextRange.create(HeredocSharedImpl.startMarkerTextOffset(markerText, marker.isIgnoringTabs()), HeredocSharedImpl.endMarkerTextOffset(markerText));
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return marker.getText();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidHeredocIdentifier(newElementName)) {//fixme check the allowed character set for heredoc markers
            throw new IncorrectOperationException("Invalid name " + newElementName);
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
