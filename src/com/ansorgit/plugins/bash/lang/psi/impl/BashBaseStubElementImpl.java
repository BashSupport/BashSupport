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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author jansorg
 */
public abstract class BashBaseStubElementImpl<T extends StubElement> extends StubBasedPsiElementBase<T> implements BashPsiElement, StubBasedPsiElement<T> {
    private final String name;

    public BashBaseStubElementImpl(final ASTNode astNode) {
        this(astNode, null);
    }

    public BashBaseStubElementImpl(final ASTNode astNode, @Nullable final String name) {
        super(astNode);
        this.name = name;
    }

    public BashBaseStubElementImpl(@NotNull T stub, @NotNull IStubElementType nodeType, @Nullable String name) {
        super(stub, nodeType);
        this.name = name;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return BashFileType.BASH_LANGUAGE;
    }

    @Override
    public String toString() {
        return "[PSI] " + (name == null ? "<undefined>" : name);
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return BashElementSharedImpl.getElementUseScope(this, getProject());
    }

    @NotNull
    @Override
    public GlobalSearchScope getResolveScope() {
        return BashElementSharedImpl.getElementGlobalSearchScope(this, getProject());
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        if (!processor.execute(this, state)) {
            return false;
        }

        return BashElementSharedImpl.walkDefinitionScope(this, processor, state, lastParent, place);
    }
}
