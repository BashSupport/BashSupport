/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashWordImpl.java, Class: BashWordImpl
 * Last modified: 2011-02-18 20:12
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
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.editor.BashEnhancedLiteralTextEscaper;
import com.ansorgit.plugins.bash.editor.BashIdentityStringLiteralEscaper;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BashWordImpl extends BashBaseStubElementImpl<StubElement> implements BashWord {
    private final static TokenSet nonWrappableChilds = TokenSet.create(BashElementTypes.STRING_ELEMENT, BashTokenTypes.STRING2, BashTokenTypes.WORD);

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

    @Override
    public boolean isWrapped() {
        String text = getText();
        return text.length() >= 2 && (text.startsWith("$'") || text.startsWith("'")) && text.endsWith("'");
    }

    @Override
    public String createEquallyWrappedString(String newContent) {
        if (!isWrapped()) {
            return newContent;
        }

        String current = getText();
        if (current.startsWith("$'")) {
            return "$'" + newContent + "'";
        } else {
            return "'" + newContent + "'";
        }
    }

    public String getUnwrappedCharSequence() {
        return getTextContentRange().substring(getText());
    }

    public boolean isStatic() {
        return BashPsiUtils.isStaticWordExpr(getFirstChild());
    }

    @NotNull
    public TextRange getTextContentRange() {
        String text = getText();

        if (text.startsWith("$'") && text.endsWith("'")) {
            return TextRange.create(2, getTextLength() - 1);
        }

        if (text.startsWith("'") && text.endsWith("'")) {
            return TextRange.create(1, getTextLength() - 1);
        }

        return TextRange.create(0, getTextLength());
    }

    @Override
    public boolean isValidHost() {
        if (!isWrapped()) {
            return false;
        }

        //fixme make sure that this refers to the eval document element
        BashCommand command = BashPsiUtils.findParent(this, BashCommand.class);
        return command != null && LanguageBuiltins.bashInjectionHostCommand.contains(command.getReferencedCommandName());
    }

    @NotNull
    @Override
    public SearchScope getUseScope() {
        return super.getUseScope();
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        PsiElement newElement = BashPsiElementFactory.createWord(getProject(), text);
        assert newElement instanceof BashWord;

        //getNode().replaceAllChildrenToChildrenOf(newElement.getNode());
        //return this;
        return BashPsiUtils.replaceElement(this, newElement);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        boolean walkOn = super.processDeclarations(processor, state, lastParent, place);

        if (walkOn && isValidHost()) {
            InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(getProject());
            List<Pair<PsiElement, TextRange>> injectedPsiFiles = injectedLanguageManager.getInjectedPsiFiles(this);
            if (injectedPsiFiles != null) {
                for (Pair<PsiElement, TextRange> psi_range : injectedPsiFiles) {
                    //fixme check lastParent ?
                    walkOn &= psi_range.first.processDeclarations(processor, state, lastParent, place);
                }
            }
        }

        return walkOn;
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        //if the word is of the form $'abc' then the content is escape-code evaluated
        //if the word is not prefixed by a dollar character then no escape code interpretation is performed

        String current = getText();

        //$' prefix -> c-escape codes are interpreted before the injected document is parsed
        if (current.startsWith("$'")) {
            return new BashEnhancedLiteralTextEscaper<BashWord>(this);
        }

        //no $' prefix -> no escape handling
        return new BashIdentityStringLiteralEscaper<BashWord>(this);
    }
}
