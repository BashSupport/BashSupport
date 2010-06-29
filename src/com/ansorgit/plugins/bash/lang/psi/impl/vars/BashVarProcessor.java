/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarProcessor.java, Class: BashVarProcessor
 * Last modified: 2010-06-30
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
                    ? PsiTreeUtil.isAncestor(BashPsiUtils.findEnclosingBlock(varDef), startElement, false)
                    : !varDef.isCommandLocal();

            if (isValid) {
                storeResult(varDef, BashPsiUtils.blockNestingLevel(varDef));
                return false;
            }
        }

        return true;
    }

    public <T> T getHint(Key<T> tKey) {
        return null;
    }
}
