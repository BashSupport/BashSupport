/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiPattern.java, Class: BashPsiPattern
 * Last modified: 2011-02-08 23:07
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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 20:33
 */
class BashPsiPattern extends PsiElementPattern<PsiElement, BashPsiPattern> {
    protected BashPsiPattern() {
        super(PsiElement.class);
    }
}

