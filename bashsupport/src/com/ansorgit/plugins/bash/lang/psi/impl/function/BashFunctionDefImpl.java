/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctionDefImpl.java, Class: BashFunctionDefImpl
 * Last modified: 2010-06-30
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl.function;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.BashSymbol;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBlockImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Icons;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * Date: 11.04.2009
 * Time: 23:48:06
 *
 * @author Joachim Ansorg
 */
public class BashFunctionDefImpl extends BashPsiElementImpl implements BashFunctionDef {
    private static final Logger log = Logger.getInstance("#Bash.BashFunctionDefImpl");

    public BashFunctionDefImpl(ASTNode astNode) {
        super(astNode, "bash function()");
    }

    public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
        if (StringUtil.isEmpty(name)) {
            return null;
        }
        //fixme validate name

        log.debug("renaming function");
        final PsiElement nameNode = getNameSymbol();
        final PsiElement newNameSymbol = BashChangeUtil.createSymbol(getProject(), name);
        log.debug("renamed to symbol " + newNameSymbol);

        getNode().replaceChild(nameNode.getNode(), newNameSymbol.getNode());
        return this;
    }

    @Override
    public String getName() {
        return getDefinedName();
    }

    public BashBlock body() {
        log.debug("found commandGroup: " + findChildByClass(BashBlockImpl.class)); //fixme
        return findChildByClass(BashBlockImpl.class);
    }

    public boolean hasCommandGroup() {
        log.debug("hasCommandGroup");
        final BashBlock body = body();

        return body != null && body.isCommandGroup();
    }

    public BashSymbol getNameSymbol() {
        final BashSymbol nameWord = findChildByClass(BashSymbol.class);
        log.debug("getNameSymbole result: " + nameWord);
        return nameWord;
    }

    public List<PsiComment> findAttachedComment() {
        return BashPsiUtils.findDocumentationElementComments(this);
    }

    @NotNull
    public List<BashVar> findReferencedParameters() {
        //call the visitor to find all uses of the parameter varaiables
        Collection<BashVar> usedVariables = PsiTreeUtil.collectElementsOfType(this, BashVar.class);

        List<BashVar> parameters = Lists.newLinkedList();

        for (BashVar var : usedVariables) {
            if (var.isParameterReference()) {
                parameters.add(var);
            }
        }

        return parameters;
    }

    public String getDefinedName() {
        final BashSymbol symbol = getNameSymbol();
        if (symbol == null) {
            return "";
        }

        return symbol.getNameString();
    }

    @Override
    public Icon getIcon(int flags) {
        return Icons.METHOD_ICON;
    }

    public int getTextOffset() {
        final ASTNode name = getNameSymbol().getNode();
        return name != null ? name.getStartOffset() : super.getTextOffset();
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            public String getPresentableText() {
                return getName() + "()";
            }

            public TextAttributesKey getTextAttributesKey() {
                return null;
            }

            public String getLocationString() {
                return null;
            }

            public Icon getIcon(boolean open) {
                return BashFunctionDefImpl.this.getIcon(Iconable.ICON_FLAG_OPEN);
            }
        };
    }

    public PsiElement getNameIdentifier() {
        return getNameSymbol();
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFunctionDef(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
