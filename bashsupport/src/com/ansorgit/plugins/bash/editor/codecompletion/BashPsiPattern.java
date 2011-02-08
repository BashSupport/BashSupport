package com.ansorgit.plugins.bash.editor.codecompletion;

import com.intellij.patterns.PsiElementPattern;
import com.intellij.psi.PsiElement;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 20:33
 */
class BashPsiPattern extends PsiElementPattern<PsiElement, BashPsiPattern> {
    protected BashPsiPattern() {
        super(PsiElement.class);
    }
}

