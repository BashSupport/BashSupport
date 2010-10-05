/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashKeywordDefaultImpl.java, Class: BashKeywordDefaultImpl
 * Last modified: 2009-12-04
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Date: 06.05.2009
 * Time: 12:19:43
 *
 * @author Joachim Ansorg
 */
public abstract class BashKeywordDefaultImpl extends BashPsiElementImpl implements PsiReference, BashKeyword {
    public BashKeywordDefaultImpl(final ASTNode astNode) {
        super(astNode);
    }

    public BashKeywordDefaultImpl(final ASTNode astNode, final String name) {
        super(astNode, name);
    }

    @Override
    public PsiReference getReference() {
        return this;
    }

    public PsiElement getElement() {
        return this;
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    @Override
    public boolean canNavigateToSource() {
        return false;
    }

    public TextRange getRangeInElement() {
        PsiElement keyword = keywordElement();
        if (keyword != null) return TextRange.from(keyword.getStartOffsetInParent(), keyword.getTextLength());

        return TextRange.from(0, getTextLength());
    }

    public PsiElement resolve() {
        return this;
    }

    public String getCanonicalText() {
        return null;
    }

    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        throw new IncorrectOperationException();
    }

    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    public boolean isReferenceTo(PsiElement element) {
        return element == this;
    }

    @NotNull
    public Object[] getVariants() {
        return new Object[0];
    }

    public boolean isSoft() {
        return false;
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                final PsiElement element = keywordElement();
                return element != null ? element.getText() : null;
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                return null;
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }
        };
    }
}
