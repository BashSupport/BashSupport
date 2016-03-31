/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.psi.PsiElement;

/**
 * Provides documentation for Bash keyword elements like "if", "while" and "for".
 *
 * @author jansorg
 */
class BashKeywordDocSource extends ClasspathDocSource {
    BashKeywordDocSource() {
        super("/documentation/internal");
    }

    @Override
    String resourceNameForElement(PsiElement element) {
        return ((BashKeyword) element).keywordElement().getText();
    }

    @Override
    boolean isValid(PsiElement element, PsiElement originalElement) {
        return element instanceof BashKeyword;
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        return null;
    }
}
