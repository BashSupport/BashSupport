/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashKeyword.java, Class: BashKeyword
 * Last modified: 2010-02-06 10:50
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

package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

/**
 * Interface to mark a class which implements a bash keyword construct.
 * <p/>
 * Date: 06.05.2009
 * Time: 12:33:51
 *
 * @author Joachim Ansorg
 */
public interface BashKeyword extends PsiReference {
    /**
     * Returns the PsiElement which is the keyword. For example the PsiElement
     * for "if" is returned if this BashKeyword is an if command implementation.
     *
     * @return The keyword PsiElement which represents the
     *         (starting) keyword of this syntax construct.
     */
    PsiElement keywordElement();
}
