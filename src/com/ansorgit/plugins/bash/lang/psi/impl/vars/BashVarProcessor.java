/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarProcessor.java, Class: BashVarProcessor
 * Last modified: 2010-07-08
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

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Date: 14.04.2009
 * Time: 17:34:42
 *
 * @author Joachim Ansorg
 */
class BashVarProcessor extends BashAbstractProcessor {
    private BashVar startElement;
    private boolean checkLocalness;
    private String varName;

    public BashVarProcessor(BashVar startElement, boolean checkLocalness) {
        this.startElement = startElement;
        this.checkLocalness = checkLocalness;
        this.varName = startElement.getReferencedName();
    }

    public boolean execute(PsiElement psiElement, ResolveState resolveState) {
        if (psiElement instanceof BashVarDef) {
            BashVarDef varDef = (BashVarDef) psiElement;

            if (!varName.equals(varDef.getName()) || startElement.equals(psiElement)) {
                //proceed with the search
                return true;
            }

            //we have the same name, so it's a possible hit
            //now check the scope
            boolean isValid = checkLocalness && varDef.isFunctionScopeLocal()
                    ? isValidLocalDefinition(varDef)
                    : isValidDefinition(varDef);

            if (isValid) {
                storeResult(varDef, BashPsiUtils.blockNestingLevel(varDef));
                return false;
            }
        }

        return true;
    }

    private boolean isValidDefinition(BashVarDef varDef) {
        if (varDef.isCommandLocal()) {
            return false;
        }

        BashFunctionDef varDefScope = BashPsiUtils.findNextVarDefFunctionDefScope(varDef);

        //first case: the definition is before the start element -> the definition is valid
        //second case: the definition is after the start element:
        //  - if startElement and varDef do NOT share a common scope -> varDef is only valid if it's inside of a function definition, i.e. global
        //  - if startElement and varDef share a scope which different from the PsiFile -> valid if the startElement is inside of a function def
        if (startElement.getTextOffset() >= varDef.getTextOffset()) {
            //the var def is only valid if the varDef is NOT inside of a nested function (our rule is: more global is better)

            BashFunctionDef startElementScope = BashPsiUtils.findNextVarDefFunctionDefScope(startElement);
            if (startElementScope == null) {
                //if the start element is on global level, then the var def has to be global, too 
                return varDefScope == null;
            }

            return varDefScope == null || varDefScope.equals(startElementScope) || !PsiTreeUtil.isAncestor(startElementScope, varDefScope, true);
        }

        //the found varDef is AFTER the startElement
        if (varDefScope == null) {
            //if varDef is on global level, then it is only valid is startElement is inside of a function definition
            return BashPsiUtils.findNextVarDefFunctionDefScope(startElement) != null;
        }

        //varDef has a valid function def scope AND comes after the start element
        //in this case it is only valid if start element is in a nested function definition inside of varDefScope
        BashFunctionDef startElementScope = BashPsiUtils.findNextVarDefFunctionDefScope(startElement);
        if (startElementScope != null) {
            return PsiTreeUtil.isAncestor(varDefScope, startElementScope, true);
        }

        return false;
    }

    /**
     * A local var def is a valid definition for our start element if it's scope contains the start
     * element.
     * <p/>
     * Also, the checked variable definition has to appear before the start element.
     *
     * @param varDef The variable definition in question
     * @return True if varDef is a valid local definition for startElement
     */
    private boolean isValidLocalDefinition(BashVarDef varDef) {
        boolean validScope = PsiTreeUtil.isAncestor(BashPsiUtils.findEnclosingBlock(varDef), startElement, false);

        return validScope && varDef.getTextOffset() < startElement.getTextOffset();
    }

    public <T> T getHint(Key<T> tKey) {
        return null;
    }
}
