/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: DocumentationAwareElement.java, Class: DocumentationAwareElement
 * Last modified: 2011-04-02 00:02
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

import com.intellij.psi.PsiComment;

import java.util.List;

/**
 * User: jansorg
 * Date: 12.02.11
 * Time: 11:56
 */
public interface DocumentationAwareElement {
    /**
     * Tries to find an attached function comment which explains what this function does.
     * A function comment has to be the previous token in the tree right before this function element.
     *
     * @return The comment psi element, if found. If unavailable null is returned.
     */
    List<PsiComment> findAttachedComment();
}
