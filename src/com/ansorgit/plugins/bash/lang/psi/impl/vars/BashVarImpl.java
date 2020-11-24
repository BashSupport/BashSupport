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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashReference;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashComposedVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarUse;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarStub;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiElementFactory;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.ReferenceProvidersRegistry;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.CachedValueProvider;
import com.intellij.psi.util.CachedValuesManager;
import com.intellij.psi.util.PsiModificationTracker;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public class BashVarImpl extends BashBaseStubElementImpl<BashVarStub> implements BashVar, BashVarUse, StubBasedPsiElement<BashVarStub> {
    public BashVarImpl(final ASTNode astNode) {
        super(astNode, "Bash-var");
    }

    public BashVarImpl(@NotNull BashVarStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, "Bash var def");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor)visitor).visitVarUse(this);
        }
        else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor,
                                       @NotNull ResolveState state,
                                       PsiElement lastParent,
                                       @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @Override
    public BashReference getReference() {
        PsiReference[] references = getReferences();
        if (references.length == 1 && references[0] instanceof BashReference) {
            return (BashReference)references[0];
        }
        return null;
    }

    @NotNull
    @Override
    public PsiReference[] getReferences() {
        return ReferenceProvidersRegistry.getReferencesFromProviders(this);
    }

    @NotNull
    @Override
    public BashReference getNeighborhoodReference() {
        return DumbService.isDumb(getProject()) ? new DumbBashVarReference(this, true) : new SmartBashVarReference(this, true);
    }

    @Override
    public final boolean isVarDefinition() {
        return false;
    }

    public PsiElement getElement() {
        return this;
    }

    @Override
    public String getName() {
        return getReferenceName();
    }

    @Override
    public PsiElement setName(@NonNls @NotNull String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidNewVariableName(newName)) {
            throw new IncorrectOperationException("Invalid variable name");
        }

        PsiElement replacement = BashPsiElementFactory.createVariable(getProject(), newName, isParameterExpansion());
        return BashPsiUtils.replaceElement(this, replacement);
    }

    public String getReferenceName() {
        return CachedValuesManager.getCachedValue(this, () -> {
            String name;
            BashVarStub stub = getStub();
            if (stub != null) {
                name = stub.getName();
            }
            else {
                name = getNameTextRange().substring(getText());
            }
            return CachedValueProvider.Result.create(name, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    /**
     * A variable which is just a single word (ABC or def) can appear in a parameter substitution block (e.g. ${ABC}).
     *
     * @return True if this variable is just a single, composed word token
     */
    @Override
    public int getPrefixLength() {
        return CachedValuesManager.getCachedValue(this, () -> {
            int prefixLength;
            BashVarStub stub = getStub();
            if (stub != null) {
                prefixLength = stub.getPrefixLength();
            }
            else {
                String text = getText();
                prefixLength = text.startsWith("\\$") ? 2 : (text.startsWith("$") ? 1 : 0);
            }

            return CachedValueProvider.Result.create(prefixLength, PsiModificationTracker.MODIFICATION_COUNT);
        });
    }

    public boolean isBuiltinVar() {
        String name = getReferenceName();
        return LanguageBuiltins.bashShellVars.contains(name) || LanguageBuiltins.bourneShellVars.contains(name);
    }

    public boolean isParameterExpansion() {
        return getPrefixLength() == 0 && (getParent() instanceof BashComposedVar || getParent() instanceof BashParameterExpansion);
    }

    public boolean isParameterReference() {
        if (getTextLength() > 2) {
            return false;
        }

        if (LanguageBuiltins.bashShellParamReferences.contains(getReferenceName())) {
            return true;
        }

        //slower fallback which checks if the parameter is  a number
        return NumberUtils.toInt(getReferenceName(), -1) >= 0;
    }

    public boolean isArrayUse() {
        if (!isParameterExpansion()) {
            return false;
        }

        PsiElement nextLeafNode = PsiTreeUtil.nextLeaf(this);
        if (nextLeafNode == null) {
            return false;
        }

        ASTNode nextLeaf = nextLeafNode.getNode();
        ASTNode nextNode = getNextSibling().getNode();
        boolean nextLeafIsSquare = nextLeaf.getElementType() == BashTokenTypes.LEFT_SQUARE;

        ASTNode prev = getNode().getTreePrev();

        if (prev != null &&
            (prev.getElementType() == BashTokenTypes.PARAM_EXPANSION_OP_HASH ||
             prev.getElementType() == BashTokenTypes.PARAM_EXPANSION_OP_HASH_HASH)) {
            return true;
        }

        //${ a[1], etc. }
        if (nextNode.getElementType() == BashElementTypes.ARITHMETIC_COMMAND && nextLeafIsSquare) {
            return true;
        }

        //${ a[*] } and ${ a[@] }
        PsiElement nextLeaf2nd = PsiTreeUtil.nextLeaf(nextLeafNode);
        if (nextLeafIsSquare && nextLeaf2nd != null) {
            IElementType next2 = nextLeaf2nd.getNode().getElementType();
            return next2 == BashTokenTypes.PARAM_EXPANSION_OP_STAR || next2 == BashTokenTypes.PARAM_EXPANSION_OP_AT;
        }

        return false;
    }

    protected TextRange getNameTextRange() {
        return TextRange.create(getPrefixLength(), getTextLength());
    }
}
