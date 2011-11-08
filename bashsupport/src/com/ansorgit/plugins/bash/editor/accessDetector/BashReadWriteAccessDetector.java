/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashReadWriteAccessDetector.java, Class: BashReadWriteAccessDetector
 * Last modified: 2011-04-30 16:33
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

package com.ansorgit.plugins.bash.editor.accessDetector;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

/**
 * User: jansorg
 * Date: 09.12.10
 * Time: 19:49
 */
public class BashReadWriteAccessDetector extends ReadWriteAccessDetector {
    @Override
    public boolean isReadWriteAccessible(PsiElement element) {
        return element instanceof BashVar || element instanceof BashVarDef;
    }

    @Override
    public boolean isDeclarationWriteAccess(PsiElement element) {
        return true;
    }

    @Override
    public Access getReferenceAccess(PsiElement referencedElement, PsiReference reference) {
        return getExpressionAccess(referencedElement);
    }

    @Override
    public Access getExpressionAccess(PsiElement expression) {
        if (expression instanceof BashVarDef && expression instanceof BashVar) {
            return Access.ReadWrite;
        }

        if (expression instanceof BashVarDef) {
            return Access.Write;
        }

        if (expression instanceof BashVar) {
            return Access.Read;
        }

        return Access.ReadWrite;
    }
}
