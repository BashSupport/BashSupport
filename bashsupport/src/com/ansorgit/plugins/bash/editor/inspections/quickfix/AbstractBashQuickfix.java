/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractBashQuickfix.java, Class: AbstractBashQuickfix
 * Last modified: 2010-01-25
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 21.05.2009
 * Time: 10:47:27
 */
abstract class AbstractBashQuickfix implements LocalQuickFix, IntentionAction {
    @NotNull
    public final String getText() {
        return getName();
    }

    @NotNull
    public String getFamilyName() {
        return "Bash";
    }

    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return file instanceof BashFile;
    }

    public boolean startInWriteAction() {
        return true;
    }

    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        invoke(project, null, descriptor.getPsiElement().getContainingFile());
    }
}
