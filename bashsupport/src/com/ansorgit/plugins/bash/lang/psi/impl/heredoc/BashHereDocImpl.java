/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHereDocImpl.java, Class: BashHereDocImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 12.04.2009
 * Time: 13:35:12
 *
 * @author Joachim Ansorg
 */
public class BashHereDocImpl extends BashPsiElementImpl implements BashHereDoc {
    public BashHereDocImpl(ASTNode astNode) {
        super(astNode, "bash hereDoc");
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

        PsiElement prevSibling = start.getPrevSibling();
        if (start.isEvaluatingVariables()) {
            return prevSibling;
        } else if (prevSibling != null) {
            return prevSibling.getPrevSibling();
        }

        return null;
    }

    @Nullable
    private BashHereDocStartMarker findStartMarkerElement() {
        BashHereDocEndMarker end = findEndMarkerElement();
        if (end == null) {
            return null;
        }

        PsiElement start = end.resolve();
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
        PsiElement start = findStartMarkerElement();
        return (start instanceof BashHereDocStartMarker) && ((BashHereDocStartMarker) start).isEvaluatingVariables();
    }

    public boolean isStrippingLeadingWhitespace() {
        PsiElement redirectElement = findRedirectElement();
        return (redirectElement != null) && "<<-".equals(redirectElement.getText());
    }
}
