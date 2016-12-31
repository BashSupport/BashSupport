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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract variable reference implementation to allow different implementations for smart and dumb mode.
 *
 * @author jansorg
 */
abstract class AbstractBashVarReference extends CachingReference implements BashReference, BindablePsiReference {
    protected final BashVarImpl bashVar;

    public AbstractBashVarReference(BashVarImpl bashVar) {
        this.bashVar = bashVar;
    }

    @Override
    public PsiElement getElement() {
        return bashVar;
    }

    @Override
    public boolean isReferenceTo(PsiElement element) {
        return super.isReferenceTo(element);
    }

    @Override
    public TextRange getRangeInElement() {
        return bashVar.getNameTextRange();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return bashVar.getReferenceName();
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidNewVariableName(newName)) {
            throw new IncorrectOperationException("Invalid variable name");
        }

        //if this is variable which doesn't have a $ or escaped \$ sign prefix
        if (bashVar.getPrefixLength() == 0) {
            return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, true));
        }

        return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, false));
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        return handleElementRename(element.getText());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

    @Override
    public String getReferencedName() {
        return bashVar.getReferenceName();
    }
}
