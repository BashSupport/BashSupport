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

package com.ansorgit.plugins.bash.editor.liveTemplates;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.codeInsight.template.TemplateContextType;
import com.intellij.lang.Language;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a live template context for Bash files.
 */
public class BashLiveTemplatesContext extends TemplateContextType {
    protected BashLiveTemplatesContext() {
        super("Bash", "Bash");
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        Language language = PsiUtilCore.getLanguageAtOffset(file, offset);
        if (language.isKindOf(BashFileType.BASH_LANGUAGE)) {
            PsiElement element = file.findElementAt(offset);
            if (element == null) {
                //if a user edits at the end of a comment at the end of a file then findElementAt returns null
                //(for yet unknown reasons)
                element = file.findElementAt(offset - 1);
            }

            return !BashPsiUtils.hasParentOfType(element, PsiComment.class, 3)
                    && !BashPsiUtils.hasParentOfType(element, BashShebang.class, 3)
                    && !BashPsiUtils.hasParentOfType(element, BashHereDoc.class, 1)
                    && !BashPsiUtils.isCommandParameterWord(element);
        }

        return false;
    }
}