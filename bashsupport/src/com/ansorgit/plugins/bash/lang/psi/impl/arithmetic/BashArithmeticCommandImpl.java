/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashArithmeticCommandImpl.java, Class: BashArithmeticCommandImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.arithmetic;

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.BashArithmeticCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

/**
 * Implementation of a bash arithmetic expression.
 * <p/>
 * User: jansorg
 * Date: 24.07.2009
 * Time: 22:21:25
 */
public class BashArithmeticCommandImpl extends BashPsiElementImpl implements BashArithmeticCommand {
    private static final Logger log = Logger.getInstance("#bash.BashArithmeticCommandImpl");

    public BashArithmeticCommandImpl(final ASTNode astNode) {
        super(astNode, "arithmetic expression");
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        //fixme: check variable usage
        return processor.execute(this, state);
    }
}
