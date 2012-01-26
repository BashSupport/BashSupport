/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashWordImpl.java, Class: BashWordImpl
 * Last modified: 2011-02-18 20:12
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

package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 21.05.2009
 * Time: 10:36:06
 */
public class BashWordImpl extends BashPsiElementImpl implements BashWord {
    private final static TokenSet nonWrappableChilds =
            TokenSet.create(BashElementTypes.STRING_ELEMENT, BashTokenTypes.STRING2, BashTokenTypes.WORD);

    public BashWordImpl(final ASTNode astNode) {
        super(astNode, "bash combined word");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitCombinedWord(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public boolean isWrappable() {
        PsiElement[] children = getChildren();
        return children.length > 1 && findChildByType(nonWrappableChilds) == null;
    }

    public String getUnwrappedCharSequence() {
        String text = getText();

        //if it is a single quoted string unqote it
        if (isStatic() && text.length() >= 2 && text.startsWith("'")) {
            return text.substring(1, text.length() - 1);
        }

        return text;
    }

    public boolean isStatic() {
        return BashPsiUtils.isStaticWordExpr(getFirstChild());
    }

    @NotNull
    public TextRange getTextContentRange() {
        String text = getText();
        if (text.startsWith("'") && text.endsWith("'")) {
            return TextRange.from(1, getTextLength() - 2);
        }

        return TextRange.from(0, getTextLength());
    }
}
