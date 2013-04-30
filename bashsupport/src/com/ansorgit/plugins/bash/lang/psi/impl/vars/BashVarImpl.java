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
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.resolve.reference.impl.CachingReference;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 14.04.2009
 * Time: 17:16:55
 *
 * @author Joachim Ansorg
 */
public class BashVarImpl extends BashPsiElementImpl implements BashVar {

    private final BashReference cachingVarReference;

    private static class CachedBashVarReference extends CachingReference implements BashReference {
        private final BashVarImpl bashVar;

        public CachedBashVarReference(BashVarImpl bashVar) {
            this.bashVar = bashVar;
        }

        @Nullable
        @Override
        public PsiElement resolveInner() {
            final String varName = bashVar.getName();
            if (varName == null) {
                return null;
            }

            BashVarProcessor processor = new BashVarProcessor(bashVar, true);
            if (!BashPsiUtils.varResolveTreeWalkUp(processor, bashVar, bashVar.getContainingFile(), ResolveState.initial())) {
                return processor.getBestResult(false, bashVar);
            }

            return null;
        }

        @NotNull
        @Override
        public String getUnresolvedMessagePattern() {
            return "unresolved var";
        }

        @Override
        public PsiElement getElement() {
            return bashVar;
        }

        @Override
        public TextRange getRangeInElement() {
            if (bashVar.isSingleWord()) {
                return TextRange.from(0, getReferencedName().length());
            }

            return TextRange.from(1, getReferencedName().length());
        }

        @NotNull
        @Override
        public String getCanonicalText() {
            return bashVar.getReferencedName();
        }

        public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
            if (!BashIdentifierUtil.isValidIdentifier(newName)) {
                throw new IncorrectOperationException("Can't have an empty name");
            }

            //if this is variable which doesn't have a $ sign prefix
            if (bashVar.isSingleWord()) {
                //return BashPsiUtils.replaceElement(this, BashChangeUtil.createWord(getProject(), newName));
                return BashPsiUtils.replaceElement(bashVar, BashChangeUtil.createVariable(bashVar.getProject(), newName, true));
            }

            return BashPsiUtils.replaceElement(bashVar, BashChangeUtil.createVariable(bashVar.getProject(), newName, false));
        }

        /*
        if (this == element) {
            return true;
        }

        if (element instanceof BashVarDef) {
            BashVarDef def = (BashVarDef) element;

            //the variable definition has to be of the same same,
            //this variable has to be in the definition's scope and finally,
            //the resolve of this variable has to be the definition (needed for local variable handling)
            return Comparing.equal(getName(), ((PsiNamedElement) element).getName())
                    && BashVarUtils.isInDefinedScope(this, def);
        }

        return false;
         */

        @Override
        public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
            throw new IncorrectOperationException("bindToElement not implemented");
        }

        @NotNull
        @Override
        public Object[] getVariants() {
            return new Object[0];  //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public String getReferencedName() {
            return bashVar.getReferencedName();
        }
    }

    public BashVarImpl(final ASTNode astNode) {
        super(astNode, "Bash-var");
        this.cachingVarReference = new CachedBashVarReference(this);
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

    public PsiElement getElement() {
        return this;
    }

    @Override
    public String getName() {
        return getReferencedName();
    }

  /*  @NotNull
    public Object[] getVariants() {
        return PsiReference.EMPTY_ARRAY;
    } */

   /* public boolean isSoft() {
        return false;
    }*/

    public String getReferencedName() {
        final String text = getText();

        return isSingleWord() ? text : text.substring(1);
    }

    /**
     * A variable which is just a single word (ABC or def) can appear is a parameter substitution block (e.g. ${ABC}).
     *
     * @return True if this variable is just a single, composed word token
     */
    private boolean isSingleWord() {
        String text = getText();
        return text.length() > 0 && text.charAt(0) != '$';
    }

    public boolean isBuiltinVar() {
        String name = getReferencedName();
        return LanguageBuiltins.bashShellVars.contains(name) || LanguageBuiltins.bourneShellVars.contains(name);
    }

    public boolean isParameterExpansion() {
        return isSingleWord() && (getParent() instanceof BashComposedVar || getParent() instanceof BashParameterExpansion);
    }

    public boolean isParameterReference() {
        if (LanguageBuiltins.bashShellParamReferences.contains(getReferencedName())) {
            return true;
        }

        //slower fallback which checks if the parameter is  a number
        int numericValue = NumberUtils.toInt(getReferencedName(), -1);
        return numericValue >= 0;

    }

    public boolean isArrayUse() {
        ASTNode next = getNode().getTreeNext();

        if (isParameterExpansion() && next.getElementType() == BashElementTypes.ARITHMETIC_COMMAND) {
            ASTNode firstChild = next.getFirstChildNode();
            return firstChild != null && firstChild.getElementType() == BashTokenTypes.LEFT_SQUARE;
        }

        return false;
    }
}
