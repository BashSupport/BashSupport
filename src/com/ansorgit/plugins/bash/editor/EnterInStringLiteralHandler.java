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

package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.intellij.codeInsight.editorActions.AutoHardWrapHandler;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegateAdapter;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This enter handler inserts a line continuation character.
 * User typing: only inserted in a string literal
 * On line wrapping: Always inserted
 *
 * @author jansorg
 */
public class EnterInStringLiteralHandler extends EnterHandlerDelegateAdapter {
    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance, @NotNull DataContext dataContext, @Nullable EditorActionHandler originalHandler) {
        if (!(file instanceof BashFile)) {
            return Result.Continue;
        }


        int offset = caretOffset.get();

        // don't wrap at EOL or EOF
        CharSequence content = editor.getDocument().getCharsSequence();
        if (offset >= content.length() || content.charAt(offset) == '\n') {
            return Result.Continue;
        }

        if (PsiDocumentManager.getInstance(file.getProject()).isUncommited(editor.getDocument())) {
            // return early if PSI is not up-to-date to avoid blocking the editor
            // this might result in line-continuations not being inserted while editing
            return Result.Continue;
        }

        PsiElement psi = file.findElementAt(offset);
        if (psi == null || psi.getNode() == null) {
            return Result.Continue;
        }

        boolean isUserTyping = !Boolean.TRUE.equals(DataManager.getInstance().loadFromDataContext(dataContext, AutoHardWrapHandler.AUTO_WRAP_LINE_IN_PROGRESS_KEY));
        if (isUserTyping && !isInString(psi)) {
            return Result.Continue;
        }

        if (offset >= psi.getTextOffset() && psi.getNode().getElementType() != BashTokenTypes.LINE_FEED) {
            EditorModificationUtil.insertStringAtCaret(editor, "\\\n");
            return Result.Stop;
        }

        return Result.Continue;
    }

    private boolean isInString(PsiElement start) {
        PsiElement parent = PsiTreeUtil.skipParentsOfType(start, LeafPsiElement.class, BashVar.class);
        if (parent != null) {
            return parent instanceof BashString;
        }
        return start instanceof BashString;
    }
}
