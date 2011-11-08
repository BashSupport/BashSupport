/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarUtils.java, Class: BashVarUtils
 * Last modified: 2011-02-10 21:44
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Jan 28, 2010
 * Time: 7:50:10 PM
 */
public class BashVarUtils {
    /**
     * Checks whether the given candidate is a valid reference to the variable definition. It is valid
     * if both are global or if the candidate is in the local scope of the variable definition.
     *
     * @param referenceElement The candidate element to check
     * @param definition       The reference definition
     * @return True if the candidate is a valid reference to the definition
     */
    public static boolean isInDefinedScope(@NotNull PsiElement referenceElement, @NotNull BashVarDef definition) {
        if (definition.isFunctionScopeLocal()) {
            //the reference is a local variable, check if the candidate is in its scope
            return PsiTreeUtil.isAncestor(definition.findFunctionScope(), referenceElement, false);
        } else if (referenceElement instanceof BashVarDef && ((BashVarDef) referenceElement).isFunctionScopeLocal()) {
            //the candidate is a local definition, check if we are in the
            //fixme check this
            return PsiTreeUtil.isAncestor(definition.findFunctionScope(), referenceElement, false);
        } else {
            //we need to check the offset of top-level elements
            if (referenceElement instanceof BashVar) {
                BashVar var = (BashVar) referenceElement;
                BashVarDef referecingDefinition = (BashVarDef) var.resolve();

                if (referecingDefinition != null && referecingDefinition.isFunctionScopeLocal()) {
                    return isInDefinedScope(referecingDefinition, definition);
                }
            }

            //make sure that the reference is not a self-reference
            if (BashPsiUtils.hasContext(referenceElement, definition)) {
                return false;
            }

            //variableDefinition may be otherwise valid but may be defined after the variable, i.e. it's invalid
            //this check is only valid if both are in the same file
            if (!BashPsiUtils.isValidReferenceScope(referenceElement, definition)) {
                return false;
            }
        }

        //none is a local variable
        return true;
    }
}
