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

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.ansorgit.plugins.bash.lang.util.HeredocSharedImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for heredoc markers.
 * <br>
 * @author jansorg
 */
abstract class AbstractHeredocMarker extends BashBaseElement implements BashHereDocMarker {
    private HeredocMarkerReference reference;

    AbstractHeredocMarker(ASTNode astNode, String name) {
        super(astNode, name);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @Override
    public String getName() {
        return getMarkerText();
    }

    @Override
    public String getMarkerText() {
        return HeredocSharedImpl.cleanMarker(getText(), isIgnoringTabs());
    }

    @Override
    public PsiElement setName(@NotNull String newElementName) throws IncorrectOperationException {
        PsiReference reference = getReference();
        return reference != null ? reference.handleElementRename(newElementName) : null;
    }

    @Override
    public PsiReference getReference() {
        if (reference == null) {
            reference = createReference();
        }

        return reference;
    }

    public abstract HeredocMarkerReference createReference();

    @NotNull
    public String getCanonicalText() {
        return getText();
    }
}
