/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashKeywordDocSource.java, Class: BashKeywordDocSource
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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.psi.PsiElement;

/**
 * Provides documentation for Bash keyword elements like "if", "while" and "for".
 * <p/>
 * Date: 06.05.2009
 * Time: 12:37:14
 *
 * @author Joachim Ansorg
 */
class BashKeywordDocSource implements DocumentationSource {
    public String documentation(PsiElement element, PsiElement originalElement) {
        if (!(element instanceof BashKeyword)) {
            return null;
        }

        return ClasspathDocumentationReader.readFromClasspath("/documentation/internal", ((BashKeyword) element).keywordElement().getText());
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        return null;
    }
}
