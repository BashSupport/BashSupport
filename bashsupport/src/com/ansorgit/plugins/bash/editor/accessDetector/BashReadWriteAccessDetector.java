package com.ansorgit.plugins.bash.editor.accessDetector;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;

/**
 * User: jansorg
 * Date: 09.12.10
 * Time: 19:49
 */
public class BashReadWriteAccessDetector extends ReadWriteAccessDetector {
    @Override
    public boolean isReadWriteAccessible(PsiElement element) {
        return element instanceof BashVar || element instanceof BashVarDef;
    }

    @Override
    public boolean isDeclarationWriteAccess(PsiElement element) {
        return true;
    }

    @Override
    public Access getReferenceAccess(PsiElement referencedElement, PsiReference reference) {
        return getExpressionAccess(referencedElement);
    }

    @Override
    public Access getExpressionAccess(PsiElement expression) {
        if (expression instanceof BashVarDef && expression instanceof BashVar) {
            return Access.ReadWrite;
        }

        if (expression instanceof BashVarDef) {
            return Access.Write;
        }

        if (expression instanceof BashVar) {
            return Access.Read;
        }

        return Access.ReadWrite;
    }
}
