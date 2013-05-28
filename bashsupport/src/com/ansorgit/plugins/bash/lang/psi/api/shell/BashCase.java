/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCase.java, Class: BashCase
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

package com.ansorgit.plugins.bash.lang.psi.api.shell;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.psi.PsiElement;

import java.util.Collection;

/**
 * Date: 06.05.2009
 * Time: 13:22:51
 *
 * @author Joachim Ansorg
 */
public interface BashCase extends BashPsiElement {
    /**
     * Returns the elements which are part of the case's pattern list.
     *
     * @return Array containing the pattern list. May be empty.
     */
    Collection<? extends PsiElement> patternList();
}
