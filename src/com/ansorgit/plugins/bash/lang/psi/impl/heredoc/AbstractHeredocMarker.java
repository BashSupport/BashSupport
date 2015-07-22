/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractHeredocMarker.java, Class: AbstractHeredocMarker
 * Last modified: 2011-04-30 16:33
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.StubElement;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for heredoc markers.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 12:48:49 PM
 */
abstract class AbstractHeredocMarker extends BashBaseStubElementImpl<StubElement> implements BashHereDocMarker {
    private static final Object[] EMPTY = new Object[0];

    private HeredocMarkerReference reference;

    public AbstractHeredocMarker(ASTNode astNode, String name) {
        super(astNode, name);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @Override
    public String getName() {
        return getMarkerText();
    }

    @Override
    public PsiElement setName(@NotNull String newElementName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newElementName)) {
            throw new IncorrectOperationException("Invalid name");
        }

        return BashPsiUtils.replaceElement(this, createMarkerElement(newElementName));
    }

    @Override
    public PsiReference getReference() {
        if (reference == null) {
            this.reference = new HeredocMarkerReference(this);
        }

        return reference;
    }

    @NotNull
    public String getCanonicalText() {
        return getText();
    }

    @Nullable
    protected abstract PsiElement resolveInner();

    protected abstract PsiElement createMarkerElement(String name);

    private static class HeredocMarkerReference extends CachingReference implements BindablePsiReference {
        private AbstractHeredocMarker marker;

        public HeredocMarkerReference(AbstractHeredocMarker marker) {
            this.marker = marker;
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            return marker.resolveInner();
        }

        @Override
        public PsiElement getElement() {
            return marker;
        }

        @Override
        public TextRange getRangeInElement() {
            String text = marker.getText();
            String markerText = marker.getMarkerText();

            return TextRange.from(text.indexOf(markerText), markerText.length());
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return marker.getText();
        }

        @Override
        public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
            return marker.setName(newElementName);
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
            return EMPTY;
        }
    }
}
