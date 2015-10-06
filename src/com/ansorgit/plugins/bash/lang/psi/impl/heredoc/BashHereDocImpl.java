/**
 * ****************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHereDocImpl.java, Class: BashHereDocImpl
 * Last modified: 2011-04-30 16:33
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ****************************************************************************
 */

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 12.04.2009
 * Time: 13:35:12
 *
 * @author Joachim Ansorg
 */
public class BashHereDocImpl extends BashBaseStubElementImpl<StubElement> implements BashHereDoc, PsiLanguageInjectionHost {
    public BashHereDocImpl(ASTNode astNode) {
        super(astNode, "bash here doc");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitHereDoc(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Nullable
    private PsiElement findRedirectElement() {
        BashHereDocStartMarker start = findStartMarkerElement();
        if (start == null) {
            return null;
        }

        for (PsiElement sibling = start.getPrevSibling(); sibling != null; sibling = sibling.getPrevSibling()) {
            if (sibling.getNode().getElementType() == BashTokenTypes.HEREDOC_MARKER_TAG) {
                return sibling;
            }
        }

        return null;
    }

    @Nullable
    private BashHereDocStartMarker findStartMarkerElement() {
        BashHereDocEndMarker end = findEndMarkerElement();
        if (end == null || end.getReference() == null) {
            return null;
        }

        PsiElement start = end.getReference().resolve();
        if (start != null && start instanceof BashHereDocStartMarker) {
            return (BashHereDocStartMarker) start;
        }

        return null;
    }

    @Nullable
    private BashHereDocEndMarker findEndMarkerElement() {
        PsiElement last = getNextSibling();
        return last instanceof BashHereDocEndMarker ? (BashHereDocEndMarker) last : null;
    }

    public boolean isEvaluatingVariables() {
        BashHereDocStartMarker start = findStartMarkerElement();
        return (start != null) && start.isEvaluatingVariables();
    }

    public boolean isStrippingLeadingWhitespace() {
        PsiElement redirectElement = findRedirectElement();

        return (redirectElement != null) && "<<-".equals(redirectElement.getText());
    }

    @Override
    public boolean isValidHost() {
        return !isEvaluatingVariables();
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        ASTNode valueNode = getNode().getFirstChildNode();
        assert valueNode instanceof LeafElement;
        ((LeafElement) valueNode).replaceWithText(text);
        return this;
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new HeredocLiteralEscaper<PsiLanguageInjectionHost>(this);
    }
}
