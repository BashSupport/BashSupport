/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarUtils.java, Class: BashVarUtils
 * Last modified: 2010-07-13
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

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

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
     * @param childCandidate     The candidate element to check
     * @param variableDefinition The reference definition
     * @return True if the candidate is a valid reference to the definition
     */
    public static boolean isInDefinedScope(PsiElement childCandidate, BashVarDef variableDefinition) {
        if (variableDefinition.isFunctionScopeLocal()) {
            //the reference is a local variable, check if the candidate is in its scope
            return PsiTreeUtil.isAncestor(variableDefinition.findFunctionScope(), childCandidate, false);
        } else if (childCandidate instanceof BashVarDef && ((BashVarDef) childCandidate).isFunctionScopeLocal()) {
            //the candidate is a local definition, check if we are in the
            //fixme check this
            return PsiTreeUtil.isAncestor(variableDefinition.findFunctionScope(), childCandidate, false);
        } else if (childCandidate instanceof BashVar) {
            BashVar var = (BashVar) childCandidate;
            BashVarDef childCandidateDef = (BashVarDef) var.resolve();

            if (childCandidateDef != null && childCandidateDef.isFunctionScopeLocal()) {
                return isInDefinedScope(childCandidateDef, variableDefinition);
            }
        }

        //none is a local variable
        return true;
    }
}
