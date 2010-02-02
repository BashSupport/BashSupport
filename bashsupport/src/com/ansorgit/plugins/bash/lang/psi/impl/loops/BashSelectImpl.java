/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSelectImpl.java, Class: BashSelectImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.loops;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.loops.BashSelect;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 06.05.2009
 * Time: 12:48:16
 *
 * @author Joachim Ansorg
 */
public class BashSelectImpl extends BashKeywordDefaultImpl implements BashSelect {
    public BashSelectImpl(final ASTNode astNode) {
        super(astNode, "bash select loop");
    }

    public PsiElement keywordElement() {
        return findChildByType(BashTokenTypes.SELECT_KEYWORD);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return BashPsiUtils.processChildDeclarations(this, processor, state, lastParent, place);
    }
}
