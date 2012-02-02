/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractHeredocMarker.java, Class: AbstractHeredocMarker
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.ResolveProcessor;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for heredoc markers.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 12:48:49 PM
 */
abstract class AbstractHeredocMarker extends BashPsiElementImpl implements BashHereDocMarker, PsiReference {
    private final Object[] EMPTY = new Object[0];
    private final Class<? extends BashPsiElement> otherEndsType;
    private final boolean expectLater;


    public AbstractHeredocMarker(ASTNode astNode, String name, @NotNull Class<? extends BashPsiElement> otherEndsType, boolean expectLater) {
        super(astNode, name);
        this.otherEndsType = otherEndsType;
        this.expectLater = expectLater;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @Override
    public String getName() {
        return getText();
    }

    public PsiElement setName(@NotNull @NonNls String newname) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newname)) {
            throw new IncorrectOperationException("The name is empty");
        }

        return BashPsiUtils.replaceElement(this, BashChangeUtil.createWord(getProject(), newname));
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public String getReferencedName() {
        return getText();
    }

    public PsiElement getElement() {
        return this;
    }

    public PsiElement getNameIdentifier() {
        return this;
    }

    public TextRange getRangeInElement() {
        return TextRange.from(0, getTextLength());
    }

    public PsiElement resolve() {
        final String varName = getText();
        if (varName == null) {
            return null;
        }

        final ResolveProcessor processor = new BashHereDocMarkerProcessor(getReferencedName(), otherEndsType);
        if (expectLater) {
            PsiTreeUtil.treeWalkUp(processor, this, this.getContainingFile(), ResolveState.initial());
        } else {
            PsiTreeUtil.treeWalkUp(processor, this, this.getContainingFile(), ResolveState.initial());
        }

        return processor.getBestResult(true, this);
    }

    public String getCanonicalText() {
        return getText();
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return setName(newElementName);
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not yet implemented");
    }

    public boolean isReferenceTo(PsiElement element) {
        return otherEndsType.isInstance(element) && element.getText().equals(getText());
    }

    @NotNull
    public Object[] getVariants() {
        return EMPTY;
    }

    public boolean isSoft() {
        return false;
    }
}
