package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.editorActions.enter.EnterHandlerDelegate;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Inserts an extra line break if a unclosed brace is closed by enter right before another function definition. The closing
 * brace has to be separated from the next function definition by a newline to be valid syntax.
 * <p>
 * See issue #89 for details.
 */
public class BashUnmatchedBraceEnterProcessor implements EnterHandlerDelegate {
    @Override
    public Result preprocessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull Ref<Integer> caretOffset, @NotNull Ref<Integer> caretAdvance, @NotNull DataContext dataContext, @Nullable EditorActionHandler originalHandler) {
        Project project = editor.getProject();

        if (CodeInsightSettings.getInstance().INSERT_BRACE_ON_ENTER && file instanceof BashFile && project != null) {
            Document document = editor.getDocument();
            CharSequence chars = document.getCharsSequence();

            int offset = caretOffset.get();
            int length = chars.length();

            if (offset < length && offset >= 1 && chars.charAt(offset - 1) == '{') {
                int start = offset + 1;
                int end = offset + 1 + "function".length();

                if (start < length && end < length && "function".contentEquals(chars.subSequence(start, end))) {
                    document.insertString(start, "\n");
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                }
            }
        }

        return Result.Continue;
    }

    @Override
    public Result postProcessEnter(@NotNull PsiFile file, @NotNull Editor editor, @NotNull DataContext dataContext) {
        return Result.Default;
    }
}
