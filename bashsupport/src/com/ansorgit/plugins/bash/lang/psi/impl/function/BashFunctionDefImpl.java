/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctionDefImpl.java, Class: BashFunctionDefImpl
 * Last modified: 2013-05-05
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
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashGroupImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * @author Joachim Ansorg
 */
public class BashFunctionDefImpl extends BashBaseStubElementImpl<BashFunctionDefStub> implements BashFunctionDef, StubBasedPsiElement<BashFunctionDefStub> {
    private static final Logger log = Logger.getInstance("#Bash.BashFunctionDefImpl");

    public BashFunctionDefImpl(ASTNode astNode) {
        super(astNode, "bash function()");
    }

    public BashFunctionDefImpl(@NotNull BashFunctionDefStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, null);
    }

    public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
        if (StringUtil.isEmpty(name)) {
            return null;
        }

        //fixme validate name

        final PsiElement nameNode = getNameSymbol();
        final PsiElement newNameSymbol = BashChangeUtil.createSymbol(getProject(), name);

        if (log.isDebugEnabled()) {
            log.debug("renamed to symbol " + newNameSymbol);
        }

        getNode().replaceChild(nameNode.getNode(), newNameSymbol.getNode());
        return this;
    }

    @Override
    public String getName() {
        return getDefinedName();
    }

    public BashBlock body() {
        if (log.isDebugEnabled()) {
            log.debug("found commandGroup: " + findChildByClass(BashGroupImpl.class)); //fixme
        }

        return findChildByClass(BashGroupImpl.class);
    }

    public boolean hasCommandGroup() {
        log.debug("hasCommandGroup");

        final BashBlock body = body();

        return body != null && body.isCommandGroup();
    }

    Key<BashFunctionDefName> NAME_SYMBOL_KEY = Key.create("nameSymbol");

    public BashFunctionDefName getNameSymbol() {
        final BashFunctionDefName nameWord = findChildByClass(BashFunctionDefName.class);

        if (log.isDebugEnabled()) {
            log.debug("getNameSymbole result: " + nameWord);
        }

        putUserData(NAME_SYMBOL_KEY, nameWord);

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

    private Key<String> DEFINED_NAME_KEY = Key.create("definedName");

    public String getDefinedName() {
        BashFunctionDefName symbol = getNameSymbol();
        String result = symbol == null ? "" : symbol.getNameString();

        putUserData(DEFINED_NAME_KEY, result);

        return result;
    }

    @Override
    public Icon getIcon(int flags) {
        return PlatformIcons.METHOD_ICON;
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

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        //walk the tree from top to bottom because the first definition has higher priority and should still be processed
        //if a later definition masks it

        PsiElement child = null;
        if (lastParent != null && lastParent.getParent() == this) {
            child = lastParent.getPrevSibling();
            if (child == null) {
                return true; // first element
            }
        }

        if (child == null) {
            child = getFirstChild();
        }

        while (child != null) {
            if (!child.processDeclarations(processor, state, null, place)) {
                return false;
            }

            if (child == lastParent) {
                break;
            }

            child = child.getNextSibling();
        }

        // If the last processed child is the parent of the place element check if we need to process
        // the elements after the element
        // In certain cases the resolving has to continue below the initial place, e.g.
        // function x() {
        //    function y() {
        //        echo $a
        //    }
        //
        //    a=
        // }

        if (child != null) {
            PsiElement functionContainer = BashPsiUtils.findParent(child.getNextSibling(), BashFunctionDef.class);
            if (functionContainer != null && functionContainer != this) {
                //process the siblings after the parent of place
                while (child != null) {
                    if (!child.processDeclarations(processor, state, null, place)) {
                        return false;
                    }

                    child = child.getNextSibling();
                }
            }
        }

        return true;
    }
}
