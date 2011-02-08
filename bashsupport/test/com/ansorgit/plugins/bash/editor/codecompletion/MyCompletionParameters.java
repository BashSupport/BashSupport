package com.ansorgit.plugins.bash.editor.codecompletion;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 19:37
 */
public class MyCompletionParameters extends CompletionParameters {
    public MyCompletionParameters(@org.jetbrains.annotations.NotNull PsiElement position, @org.jetbrains.annotations.NotNull PsiFile originalFile, CompletionType completionType, int offset, int invocationCount) {
        super(position, originalFile, completionType, offset, invocationCount);
    }
}
