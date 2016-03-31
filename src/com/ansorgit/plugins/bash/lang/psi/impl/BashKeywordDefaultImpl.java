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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author jansorg
 */
public abstract class BashKeywordDefaultImpl extends BashCompositeElement implements BashKeyword {
    protected BashKeywordDefaultImpl(IElementType type) {
        super(type);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return BashResolveUtil.processContainerDeclarations(this, processor, state, lastParent, place);
    }

    @Override
    public PsiReference getReference() {
        //a reference is required for QuickDoc support, camMavigate avoids the "Go to definition" nvaigation
        return BashPsiUtils.selfReference(this);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new KeywordPresentation(keywordElement());
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    private class KeywordPresentation implements ItemPresentation {
        private final PsiElement element;

        KeywordPresentation(PsiElement element) {
            this.element = element;
        }

        public String getPresentableText() {
            return element != null ? element.getText() : null;
        }

        public String getLocationString() {
            return null;
        }

        public Icon getIcon(boolean open) {
            return null;
        }
    }
}
