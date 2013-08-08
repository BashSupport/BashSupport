/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashBlockImpl.java, Class: BashBlockImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.jetbrains.PsiScopesUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Joachim Ansorg
 */
public class BashGroupImpl extends BashCompositeElement implements BashBlock {
    public BashGroupImpl() {
        super(BashElementTypes.GROUP_ELEMENT);
    }

    public boolean isCommandGroup() {
        return true;
    }

    public PsiElement commandGroup() {
        return this;
    }

    private boolean isFunctionBody() {
        return getParent() instanceof BashFunctionDef;
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        if (isFunctionBody()) {
            return BashElementSharedImpl.walkDefinitionScope(this, processor, state, lastParent, place);
        }

        return PsiScopesUtil.walkChildrenScopes(this, processor, state, lastParent, place);
    }
}
