/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashInspection.java, Class: AbstractBashInspection
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

package com.ansorgit.plugins.bash.editor.inspections.inspections;

import com.intellij.codeHighlighting.HighlightDisplayLevel;
import com.intellij.codeInspection.CustomSuppressableInspectionTool;
import com.intellij.codeInspection.LocalInspectionTool;
import com.intellij.codeInspection.SuppressIntentionAction;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstract base class for Bash inspections.
 * <p/>
 * Date: 15.05.2009
 * Time: 14:42:11
 *
 * @author Joachim Ansorg
 */
abstract class AbstractBashInspection extends LocalInspectionTool implements CustomSuppressableInspectionTool {
    private static final SuppressIntentionAction[] EMPTY_ARRAY = new SuppressIntentionAction[0];

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @NotNull
    public HighlightDisplayLevel getDefaultLevel() {
        return HighlightDisplayLevel.INFO;
    }

    public boolean isSuppressedFor(PsiElement element) {
        return false;
    }

    public SuppressIntentionAction[] getSuppressActions(@Nullable PsiElement element) {
        return EMPTY_ARRAY;
    }

    @Nls
    @NotNull
    public String getGroupDisplayName() {
        return "Bash";
    }
}
