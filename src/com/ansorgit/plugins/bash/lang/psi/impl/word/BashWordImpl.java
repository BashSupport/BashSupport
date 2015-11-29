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
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashScopeProcessor;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashElementSharedImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

public class BashWordImpl extends BashBaseStubElementImpl<StubElement> implements BashWord, PsiLanguageInjectionHost, BashLanguageInjectionHost {
    private final static TokenSet nonWrappableChilds = TokenSet.create(BashElementTypes.STRING_ELEMENT, BashTokenTypes.STRING2, BashTokenTypes.WORD);
    private Boolean isWrapped;

    private boolean singleChildParent;
    private boolean singleChildParentComputed = false;

    public BashWordImpl(final ASTNode astNode) {
        super(astNode, "bash combined word");
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.isWrapped = null;
        this.singleChildParentComputed = false;
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
        if (isSingleChildParent()) {
            return false;
        }

        ASTNode node = getNode();
        for (ASTNode child = node.getFirstChildNode(); child != null; child = child.getTreeNext()) {
            if (nonWrappableChilds.contains(child.getElementType())) {
                return false;
            }
        }

        return true;
    }

    private boolean isSingleChildParent() {
        if (!singleChildParentComputed) {
            singleChildParent = BashPsiUtils.isSingleChildParent(this);
            singleChildParentComputed = true;
        }

        return singleChildParent;
    }

    @Override
    public boolean isWrapped() {
        if (isWrapped == null) {
            isWrapped = false;
            if (getTextLength() >= 2) {
                ASTNode firstChildNode = getNode().getFirstChildNode();
                if (firstChildNode != null && firstChildNode.getTextLength() >= 2) {
                    String text = firstChildNode.getText();

                    isWrapped = (text.startsWith("$'") || text.startsWith("'")) && text.endsWith("'");
                }
            }
        }

        return isWrapped;
    }

    @Override
    public String createEquallyWrappedString(String newContent) {
        if (!isWrapped()) {
            return newContent;
        }

        String firstText = getNode().getFirstChildNode().getText();
        if (firstText.startsWith("$'")) {
            return "$'" + newContent + "'";
        } else {
            return "'" + newContent + "'";
        }
    }

    public String getUnwrappedCharSequence() {
        return getTextContentRange().substring(getText());
    }

    public boolean isStatic() {
        return isWrapped() || BashPsiUtils.isStaticWordExpr(getFirstChild());
    }

    @NotNull
    public TextRange getTextContentRange() {
        if (!isWrapped()) {
            return TextRange.from(0, getTextLength());
        }

        ASTNode node = getNode();
        String first = node.getFirstChildNode().getText();
        String last = node.getLastChildNode().getText();

        int textLength = getTextLength();

        if (first.startsWith("$'") && last.endsWith("'")) {
            return TextRange.from(2, textLength - 3);
        }

        return TextRange.from(1, textLength - 2);
    }

    @Override
    public boolean isValidHost() {
        //only mark text wrapped in '' as valid injection containers
        return isWrapped();
    }

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        return ElementManipulators.handleContentChange(this, text);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        if (!processor.execute(this, state)) {
            return false;
        }

        if (isSingleChildParent() && isWrapped()) {
            return true;
        }

        boolean walkOn = BashElementSharedImpl.walkDefinitionScope(this, processor, state, lastParent, place);
        if (walkOn && (processor instanceof BashScopeProcessor ? isValidBashLanguageHost() : isValidHost())) {
            walkOn = InjectionUtils.walkInjection(this, processor, state, lastParent, place, true);
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
            return new BashEnhancedLiteralTextEscaper<BashWordImpl>(this);
        }

        //no $' prefix -> no escape handling
        return new BashIdentityStringLiteralEscaper<BashWordImpl>(this);
    }

    @Override
    public boolean isValidBashLanguageHost() {
        if (!isWrapped()) {
            return false;
        }

        BashCommand command = BashPsiUtils.findParent(this, BashCommand.class);
        return command != null && command.isLanguageInjectionContainerFor(this);
    }
}
