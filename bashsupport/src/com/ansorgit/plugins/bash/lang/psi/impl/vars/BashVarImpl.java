/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarImpl.java, Class: BashVarImpl
 * Last modified: 2010-07-12
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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashChangeUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Collection;
import java.util.List;

/**
 * Date: 14.04.2009
 * Time: 17:16:55
 *
 * @author Joachim Ansorg
 */
public class BashVarImpl extends BashPsiElementImpl implements BashVar {
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
        return this;
    }

    public PsiElement getElement() {
        return this;
    }

    public TextRange getRangeInElement() {
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
        if (varName == null) {
            return null;
        }

        BashVarProcessor processor = new BashVarProcessor(this, true);
        if (!BashPsiUtils.varResolveTreeWalkUp(processor, this, getContainingFile(), ResolveState.initial())) {
            return processor.getBestResult(false, this);
        }

        return null;
    }

    @NotNull
    public String getCanonicalText() {
        return getReferencedName();
    }

    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        if (!BashIdentifierUtil.isValidIdentifier(newName)) {
            throw new IncorrectOperationException("Can't have an empty name");
        }

        //if this is variable which doesn't have a $ sign prefix
        if (isSingleWord()) {
            //return BashPsiUtils.replaceElement(this, BashChangeUtil.createWord(getProject(), newName));
            return BashPsiUtils.replaceElement(this, BashChangeUtil.createVariable(getProject(), newName, true));
        }

        return BashPsiUtils.replaceElement(this, BashChangeUtil.createVariable(getProject(), newName, false));
    }

    public PsiElement bindToElement(@NotNull PsiElement psiElement) throws IncorrectOperationException {
        throw new IncorrectOperationException("bindToElement not implemented");
    }

    public boolean isReferenceTo(PsiElement element) {
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
    }

    @NotNull
    public Object[] getVariants() {
        List<Object> variants = Lists.newArrayList();

        //collect the previously declared variables
        final BashVarVariantsProcessor processor = new BashVarVariantsProcessor(this);
        PsiTreeUtil.treeWalkUp(processor, this, this.getContainingFile(), ResolveState.initial());

        variants.addAll(createPsiItems(processor.getVariables()));

        if (BashProjectSettings.storedSettings(getProject()).isAutocompleteBuiltinVars()) {
            variants.addAll(createItems(LanguageBuiltins.bashShellVars, BashIcons.BASH_VAR_ICON));
            variants.addAll(createItems(LanguageBuiltins.bourneShellVars, BashIcons.BOURNE_VAR_ICON));
        }

        if (BashProjectSettings.storedSettings(getProject()).isAutcompleteGlobalVars()) {
            variants.addAll(createItems(BashProjectSettings.storedSettings(getProject()).getGlobalVariables(), BashIcons.GLOBAL_VAR_ICON));
        }

        return variants.toArray();
    }

    private static Collection<LookupElement> createPsiItems(Collection<? extends PsiNamedElement> elements) {
        return Collections2.transform(elements, new Function<PsiNamedElement, LookupElement>() {
            public LookupElement apply(PsiNamedElement from) {
                return LookupElementBuilder.create(from).setCaseSensitive(true);
            }
        });
    }

    private static Collection<LookupElement> createItems(Collection<String> globalVars, final Icon icon) {
        return Lists.transform(Lists.newArrayList(globalVars), new Function<String, LookupElement>() {
            public LookupElement apply(String from) {
                return LookupElementBuilder.create(from).setCaseSensitive(true).setIcon(icon);
            }
        });
    }

    public boolean isSoft() {
        return false;
    }

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
}
