/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractWordWrapQuickfix.java, Class: AbstractWordWrapQuickfix
 * Last modified: 2009-12-04
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

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

/**
 * Abstract base class to wrap words in something else.
 * User: jansorg
 * Date: 21.05.2009
 * Time: 11:05:40
 */
abstract class AbstractWordWrapQuickfix extends AbstractBashQuickfix {
    protected final BashWord word;

    public AbstractWordWrapQuickfix(BashWord word) {
        this.word = word;
    }

    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile) throws IncorrectOperationException {
        Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
        if (document != null) {
            int endOffset = word.getTextOffset() + word.getTextLength();
            document.replaceString(word.getTextOffset(), endOffset, wrapText(word.getText()));
            PsiDocumentManager.getInstance(project).commitDocument(document);
        }
    }

    protected abstract String wrapText(String text);
}
