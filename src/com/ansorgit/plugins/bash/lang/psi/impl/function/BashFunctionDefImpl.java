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

package com.ansorgit.plugins.bash.lang.psi.impl.function;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBlock;
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashFunctionDefStub;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author jansorg
 */
public class BashFunctionDefImpl extends BashBaseStubElementImpl<BashFunctionDefStub> implements BashFunctionDef, StubBasedPsiElement<BashFunctionDefStub> {
    private final Object stateLock = new Object();
    private final FunctionDefPresentation presentation = new FunctionDefPresentation(this);
    private volatile BashBlock body;
    private volatile boolean computedBody = false;
    private volatile List<BashPsiElement> referencedParameters;
    private volatile Set<String> localScopeVariables;

    public BashFunctionDefImpl(ASTNode astNode) {
        super(astNode, "bash function()");
    }

    public BashFunctionDefImpl(@NotNull BashFunctionDefStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, null);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        synchronized (stateLock) {
            this.computedBody = false;
            this.body = null;
            this.referencedParameters = null;
            this.localScopeVariables = null;
        }
    }

    public PsiElement setName(@NotNull @NonNls String name) throws IncorrectOperationException {
        if (StringUtil.isEmpty(name)) {
            return null;
        }

        //fixme validate name

        final PsiElement nameNode = getNameSymbol();
        if (nameNode == null) {
            throw new IncorrectOperationException("invalid name");
        }

        final PsiElement newNameSymbol = BashPsiElementFactory.createSymbol(getProject(), name);

        getNode().replaceChild(nameNode.getNode(), newNameSymbol.getNode());
        return this;
    }

    @Override
    public String getName() {
        return getDefinedName();
    }

    public BashBlock functionBody() {
        if (!computedBody) {
            synchronized (stateLock) {
                if (!computedBody) {
                    body = findChildByClass(BashBlock.class);
                    computedBody = true;
                }
            }
        }

        return body;
    }

    public BashFunctionDefName getNameSymbol() {
        return findChildByClass(BashFunctionDefName.class);
    }

    @Nullable
    public List<PsiComment> findAttachedComment() {
        return BashPsiUtils.findDocumentationElementComments(this);
    }

    @NotNull
    public List<BashPsiElement> findReferencedParameters() {
        if (referencedParameters == null) {
            synchronized (stateLock) {
                if (referencedParameters == null) {
                    //call the visitor to find all uses of the parameter variables, take care no to collect parameters used in inner functions
                    List<BashPsiElement> newReferencedParameters = Lists.newLinkedList();

                    for (BashVar var : PsiTreeUtil.collectElementsOfType(this, BashVar.class)) {
                        if (var.isParameterReference() && this.equals(BashPsiUtils.findParent(var, BashFunctionDef.class, BashFunctionDef.class))) {
                            newReferencedParameters.add(var);
                        }
                    }

                    referencedParameters = newReferencedParameters;
                }
            }
        }

        return referencedParameters;
    }

    @NotNull
    @Override
    public Set<String> findLocalScopeVariables() {
        if (localScopeVariables == null) {
            synchronized (stateLock) {
                if (localScopeVariables == null) {
                    localScopeVariables = Sets.newLinkedHashSetWithExpectedSize(10);

                    Collection<BashVarDef> varDefs = PsiTreeUtil.findChildrenOfType(this, BashVarDef.class);
                    for (BashVarDef varDef : varDefs) {
                        if (varDef.isLocalVarDef() && this.isEquivalentTo(BashPsiUtils.findNextVarDefFunctionDefScope(varDef))) {
                            localScopeVariables.add(varDef.getReferenceName());
                        }
                    }
                }
            }
        }

        return localScopeVariables;
    }

    public String getDefinedName() {
        BashFunctionDefStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        BashFunctionDefName symbol = getNameSymbol();

        return symbol == null ? "" : symbol.getNameString();
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
        return presentation;
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
        if (lastParent != null && lastParent.equals(functionBody())) {
            return processor.execute(this, state);
        }

        return BashResolveUtil.processContainerDeclarations(this, processor, state, lastParent, place);
    }

    private static class FunctionDefPresentation implements ItemPresentation {
        private final BashFunctionDef function;

        FunctionDefPresentation(BashFunctionDefImpl functionDef) {
            this.function = functionDef;
        }

        public String getPresentableText() {
            return function.getName() + "()";
        }

        public String getLocationString() {
            return null;
        }

        public Icon getIcon(boolean open) {
            return null;
        }
    }
}
