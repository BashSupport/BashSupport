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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulator;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract function reference to allow implementations for smart and dumb mode.
 *
 * @author jansorg
 */
abstract class AbstractFunctionReference extends CachingReference implements BashReference, BindablePsiReference {
    protected final AbstractBashCommand<?> cmd;

    public AbstractFunctionReference(AbstractBashCommand<?> cmd) {
        this.cmd = cmd;
    }

    @Override
    public String getReferencedName() {
        return cmd.getReferencedCommandName();
    }

    @Override
    public PsiElement getElement() {
        return cmd;
    }

    @Override
    public TextRange getRangeInElement() {
        return getManipulator().getRangeInElement(cmd);
    }

    @NotNull
    private ElementManipulator<AbstractBashCommand<?>> getManipulator() {
        ElementManipulator<AbstractBashCommand<?>> manipulator = ElementManipulators.<AbstractBashCommand<?>>getManipulator(cmd);
        if (manipulator == null) {
            throw new IncorrectOperationException("No element manipulator found for " + cmd);
        }
        return manipulator;
    }

    @NotNull
    @Override
    public String getCanonicalText() {
        String referencedName = cmd.getReferencedCommandName();
        return referencedName != null ? referencedName : "";
    }

    @Override
    public PsiElement handleElementRename(String newElementName) throws IncorrectOperationException {
        return getManipulator().handleContentChange(cmd, newElementName);
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (element instanceof BashFunctionDef) {
            return handleElementRename(((BashFunctionDef) element).getName());
        }

        throw new IncorrectOperationException("unsupported for element " + element);
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
