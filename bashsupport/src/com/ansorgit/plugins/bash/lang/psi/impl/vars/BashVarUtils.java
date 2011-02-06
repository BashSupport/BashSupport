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

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

import java.util.List;

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

            //variableDefinition may be otherwise valid but may be defined after the variable, i.e. it's invalid
            //this check is only valid if both are in the same file
            boolean sameFile = variableDefinition.getContainingFile().equals(var.getContainingFile());
            if (sameFile) {
                if (variableDefinition.getTextOffset() > var.getTextOffset()) {
                    //it's an invalid reference if both variables are global and the definition is after
                    //the variable usage

                    if (isGlobal(variableDefinition) && isGlobal(var)) {
                        return false;
                    }
                }
            } else {
                //we need to find the include command and check the offset
                //the include command must fullfil the same condition as the normal variable definition above:
                //either var use and definition are both in functions or it the use is invalid
                List<BashCommand> includeCommands = BashPsiUtils.findIncludeCommands(childCandidate.getContainingFile(), variableDefinition.getContainingFile());

                //currently we do onyl support global include commands

                for (BashCommand includeCommand : includeCommands) {
                    if (includeCommand.getTextOffset() > var.getTextOffset()) {
                        if (isGlobal(includeCommand) && isGlobal(var)) {
                            return false;
                        }
                    }
                }
            }
        }

        //none is a local variable
        return true;
    }

    private static boolean isGlobal(PsiElement element) {
        return BashPsiUtils.findBroadestVarDefFunctionDefScope(element) == null;
    }
}
