/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashQuickfix.java, Class: AbstractBashQuickfix
 * Last modified: 2010-12-28 14:57
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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.editor.inspections.InspectionProvider;
import com.intellij.codeInspection.LocalQuickFixAndIntentionActionOnPsiElement;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class for Bash quickfixes.
 * <p/>
 * User: jansorg
 * Date: 21.05.2009
 * Time: 10:47:27
 */
abstract class AbstractBashPsiElementQuickfix extends LocalQuickFixAndIntentionActionOnPsiElement {
    protected AbstractBashPsiElementQuickfix(PsiElement element) {
        super(element);
    }

    @NotNull
    public String getFamilyName() {
        return InspectionProvider.FAMILY;
    }

    public boolean startInWriteAction() {
        return true;
    }
}
