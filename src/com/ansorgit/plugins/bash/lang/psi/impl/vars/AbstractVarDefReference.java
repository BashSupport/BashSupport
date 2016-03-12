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
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract variable definition reference to allow implementations for smart and dumb mode.
 *
 * @author jansorg
 */
abstract class AbstractVarDefReference extends CachingReference implements BashReference, BindablePsiReference {
    protected final BashVarDefImpl bashVarDef;

    public AbstractVarDefReference(BashVarDefImpl bashVarDef) {
        this.bashVarDef = bashVarDef;
    }

    @Override
    public String getReferencedName() {
        return bashVarDef.getReferenceName();
    }

    @Override
    public PsiElement getElement() {
        return bashVarDef;
    }

    @Override
    public TextRange getRangeInElement() {
        return bashVarDef.getAssignmentNameTextRange();
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        return bashVarDef.getReferenceName();
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        bashVarDef.setName(newElementName);
        return bashVarDef;
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (isReferenceTo(element)) {
            return bashVarDef;
        }

        //fixme right?
        return handleElementRename(element.getText());
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
