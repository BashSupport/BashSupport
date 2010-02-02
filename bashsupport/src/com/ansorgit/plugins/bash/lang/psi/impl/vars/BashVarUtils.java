/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarUtils.java, Class: BashVarUtils
 * Last modified: 2010-01-28
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

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * User: jansorg
 * Date: Jan 28, 2010
 * Time: 7:50:10 PM
 */
public class BashVarUtils {
    public static boolean isInDefinedScope(PsiElement childCandidate, BashVarDef variableDefinition) {
        PsiElement varDefContainer = BashPsiUtils.findEnclosingBlock(variableDefinition) instanceof BashFile
                ? variableDefinition
                : BashPsiUtils.findEnclosingBlock(variableDefinition);

        boolean varIsLocalDef = childCandidate instanceof BashVarDef && ((BashVarDef) childCandidate).isFunctionScopeLocal();

        return variableDefinition.isFunctionScopeLocal() || varIsLocalDef
                ? PsiTreeUtil.isAncestor(varDefContainer, childCandidate, false)
                : true;
    }
}
