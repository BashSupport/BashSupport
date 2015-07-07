/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStringImpl.java, Class: BashStringImpl
 * Last modified: 2011-02-18 20:22
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

import com.ansorgit.plugins.bash.editor.BashSimpleTextLiteralEscaper;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A string spanning start and end markers and content elements.
 * <p/>
 * Date: 12.04.2009
 * Time: 13:13:15
 *
 * @author Joachim Ansorg
 */
public class BashStringImpl extends BashBaseStubElementImpl<StubElement> implements BashString, BashCharSequence {
    public BashStringImpl(ASTNode node) {
        super(node, "Bash string");
    }

    @Override
    public boolean isWrapped() {
        String text = getText();
        return text.length() >= 2 && (text.startsWith("\"") || text.startsWith("$\"")) && text.endsWith("\"");
    }

    @Override
    public String createEquallyWrappedString(String newContent) {
        if (getText().startsWith("$\"")) {
            return "$\"" + newContent + "\"";
        }

        return "\"" + newContent + "\"";
    }

    public String getUnwrappedCharSequence() {
        return getTextContentRange().substring(getText());
    }

    public boolean isStatic() {
        return BashPsiUtils.isStaticWordExpr(getFirstChild());
    }

    @NotNull
    public TextRange getTextContentRange() {
        if (getText().startsWith("$\"")) {
            return TextRange.create(2, getTextLength() - 1);
        }

        return TextRange.create(1, getTextLength() - 1);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitString(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean isValidHost() {
        BashCommand command = BashPsiUtils.findParent(this, BashCommand.class);
        return command != null && LanguageBuiltins.bashInjectionHostCommand.contains(command.getReferencedCommandName());
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

    @Override
    public PsiLanguageInjectionHost updateText(@NotNull String text) {
        PsiElement string = BashPsiElementFactory.createString(getProject(), text);
        assert string instanceof BashString;

        return BashPsiUtils.replaceElement(this, string);
    }

    @NotNull
    @Override
    public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
        return new BashSimpleTextLiteralEscaper<BashString>(this);
    }
}
