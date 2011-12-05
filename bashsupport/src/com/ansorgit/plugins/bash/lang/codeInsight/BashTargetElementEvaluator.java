/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTargetElementEvaluator.java, Class: BashTargetElementEvaluator
 * Last modified: 2011-04-02 00:26
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

package com.ansorgit.plugins.bash.lang.codeInsight;

import com.intellij.codeInsight.TargetElementEvaluator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 01.04.11
 * Time: 23:28
 */
public class BashTargetElementEvaluator implements TargetElementEvaluator {
    public boolean includeSelfInGotoImplementation(@NotNull PsiElement element) {
        return true;
    }

    public PsiElement getElementByReference(PsiReference psiReference, int flags) {
        return null;
    }
}
