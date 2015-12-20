/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarImpl.java, Class: BashVarImpl
 * Last modified: 2013-04-30
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
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.refactoring.rename.BindablePsiReference;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 14.04.2009
 * Time: 17:16:55
 *
 * @author Joachim Ansorg
 */
public class BashVarImpl extends BashBaseStubElementImpl<BashVarStub> implements BashVar, BashVarUse, StubBasedPsiElement<BashVarStub> {
    private static final Object[] OBJECTS_EMPTY = new Object[0];

    private final BashReference cachingVarReference;
    private int prefixLength = -1;
    private String referencedName;
    private TextRange nameTextRange;

    public BashVarImpl(final ASTNode astNode) {
        super(astNode, "Bash-var");
        this.cachingVarReference = new CachedBashVarReference(this);
    }

    public BashVarImpl(@NotNull BashVarStub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType, "Bash var def");
        this.cachingVarReference = new CachedBashVarReference(this);
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.prefixLength = -1;
        this.referencedName = null;
        this.nameTextRange = null;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitVarUse(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        return processor.execute(this, state);
    }

    @NotNull
    @Override
    public BashReference getReference() {
        return cachingVarReference;
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
        if (!BashIdentifierUtil.isValidIdentifier(newName)) {
            throw new IncorrectOperationException("can't have an empty name");
        }

        PsiElement replacement = BashPsiElementFactory.createVariable(getProject(), newName, isParameterExpansion());
        return BashPsiUtils.replaceElement(this, replacement);
    }

    public String getReferenceName() {
        BashVarStub stub = getStub();
        if (stub != null) {
            return stub.getName();
        }

        if (referencedName == null) {
            referencedName = getNameTextRange().substring(getText());
        }

        return referencedName;
    }

    /**
     * A variable which is just a single word (ABC or def) can appear in a parameter substitution block (e.g. ${ABC}).
     *
     * @return True if this variable is just a single, composed word token
     */
    @Override
    public int getPrefixLength() {
        BashVarStub stub = getStub();
        if (stub != null) {
            return stub.getPrefixLength();
        }

        if (prefixLength == -1) {
            String text = getText();
            prefixLength = text.startsWith("\\$") ? 2 : (text.startsWith("$") ? 1 : 0);
        }

        return prefixLength;
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
        ASTNode prev = getNode().getTreePrev();
        if (prev != null && isParameterExpansion() && prev.getElementType() == BashTokenTypes.PARAM_EXPANSION_OP_HASH) {
            return true;
        }

        ASTNode next = getNode().getTreeNext();
        if (next != null && isParameterExpansion() && next.getElementType() == BashElementTypes.ARITHMETIC_COMMAND) {
            ASTNode firstChild = next.getFirstChildNode();
            return firstChild != null && firstChild.getElementType() == BashTokenTypes.LEFT_SQUARE;
        }

        return false;
    }

    protected TextRange getNameTextRange() {
        if (nameTextRange == null) {
            nameTextRange = TextRange.create(getPrefixLength(), getTextLength());
        }

        return nameTextRange;
    }

    private static class CachedBashVarReference extends CachingReference implements BashReference, BindablePsiReference {
        private final BashVarImpl bashVar;

        public CachedBashVarReference(BashVarImpl bashVar) {
            this.bashVar = bashVar;
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            return BashResolveUtil.resolve(bashVar, true);
        }

        @Override
        public PsiElement getElement() {
            return bashVar;
        }

        @Override
        public boolean isReferenceTo(PsiElement element) {
            return super.isReferenceTo(element);
        }

        @Override
        public TextRange getRangeInElement() {
            return bashVar.getNameTextRange();
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return bashVar.getReferenceName();
        }

        public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
            if (!BashIdentifierUtil.isValidIdentifier(newName)) {
                throw new IncorrectOperationException("Can't have an empty name");
            }

            //if this is variable which doesn't have a $ or escaped \$ sign prefix
            if (bashVar.getPrefixLength() == 0) {
                return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, true));
            }

            return BashPsiUtils.replaceElement(bashVar, BashPsiElementFactory.createVariable(bashVar.getProject(), newName, false));
        }

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            return handleElementRename(element.getText());
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return OBJECTS_EMPTY;
        }

        @Override
        public String getReferencedName() {
            return bashVar.getReferenceName();
        }
    }
}
