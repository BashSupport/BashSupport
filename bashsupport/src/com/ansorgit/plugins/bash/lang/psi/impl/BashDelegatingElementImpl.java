/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashDelegatingElementImpl.java, Class: BashDelegatingElementImpl
 * Last modified: 2010-01-25
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

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * A psi element base implementation which delegates the actions to its children.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 10:54:35 AM
 */
public abstract class BashDelegatingElementImpl extends BashPsiElementImpl {
    protected BashDelegatingElementImpl(ASTNode astNode, String name) {
        super(astNode, name);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        for (PsiElement c : this.getChildren()) {
            boolean proceed = c.processDeclarations(processor, state, lastParent, place);
            if (!proceed) {
                return false;
            }
        }

        return true;
    }
}
