/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarImpl.java, Class: BashVarImpl
 * Last modified: 2010-02-17
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
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashComposedVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashResolveUtil;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Date: 14.04.2009
 * Time: 17:16:55
 *
 * @author Joachim Ansorg
 */
public class BashVarImpl extends BashPsiElementImpl implements BashVar {
    //private static final Logger log = Logger.getInstance("#bash.BashVarImpl");

    public BashVarImpl(final ASTNode astNode) {
        super(astNode, "Bash-var");
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

    @Override
    public PsiReference getReference() {
        //log.debug("getReference");
        return this;
    }

    public PsiElement getElement() {
        //log.debug("getElement");
        return this;
    }

    public TextRange getRangeInElement() {
        //log.debug("getRangeInElement");
        if (isSingleWord()) {
            return TextRange.from(0, getReferencedName().length());
        }

        return TextRange.from(1, getReferencedName().length()); //fixme make sure it has the end } ?
    }

    @Override
    public String getName() {
        return getReferencedName();
    }

    public PsiElement resolve() {
        final String varName = getName();
        if (varName == null) return null;

        final BashVarProcessor processor = new BashVarProcessor(varName, this);
        return BashResolveUtil.treeWalkUp(processor, this, null, this, false, true);
    }

    public String getCanonicalText() {
        return null;
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newName)) {
            throw new IncorrectOperationException("Can't have an empty name");
        }

        //if this is variable which doesn't have a $ sign prefix
        if (isSingleWord()) {
            return replace(BashChangeUtil.createWord(getProject(), newName));
        }

        return replace(BashChangeUtil.createVariable(getProject(), newName, false));
    }

    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        throw new IncorrectOperationException("unimplemented");
    }

    public boolean isReferenceTo(PsiElement element) {
        if (element instanceof BashVarDef) {
            BashVarDef def = (BashVarDef) element;

            return Comparing.equal(getName(), ((PsiNamedElement) element).getName())
                    && BashVarUtils.isInDefinedScope(this, def);
        }

        return false;
    }

    @NotNull
    public Object[] getVariants() {
        List<Object> variants = Lists.newArrayList();

        //collect the previously declared variables
        final BashVarVariantsProcessor processor = new BashVarVariantsProcessor(this);
        BashResolveUtil.treeWalkUp(processor, this, null, this, false, true);

        variants.addAll(processor.getVariables());

        if (BashProjectSettings.storedSettings(getProject()).isAutocompleteBuiltinVars()) {
            variants.addAll(LanguageBuiltins.bashShellVars);
            variants.addAll(LanguageBuiltins.bourneShellVars);
        }

        if (BashProjectSettings.storedSettings(getProject()).isAutcompleteGlobalVars()) {
            variants.addAll(BashProjectSettings.storedSettings(getProject()).getGlobalVariables());
        }

        return variants.toArray();
    }

    public boolean isSoft() {
        return false;
    }

    public String getReferencedName() {
        final String text = getText();
        //log.debug("getReferencedName. text: " + text);

        return isSingleWord() ? text : text.substring(1);
    }

    /**
     * A variable which is just a single word (ABC or def) can appear is a var substitution block (e.g. ${ABC}).
     *
     * @return True if this variable is just a single, composed word token
     */
    private boolean isSingleWord() {
        return getText().length() > 0 && getText().charAt(0) != '$';
    }

    public boolean isBuiltinVar() {
        String name = getReferencedName();
        return LanguageBuiltins.bashShellVars.contains(name)
                || LanguageBuiltins.bourneShellVars.contains(name);
    }

    public boolean isComposedVar() {
        return isSingleWord() && getParent() instanceof BashComposedVar;
    }
}
