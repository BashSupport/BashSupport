package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InjectionUtils {
    private InjectionUtils() {
    }

    public static boolean walkInjection(PsiElement host, @NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place, boolean walkOn) {
        //fixme does this work on the escaped or unescpaed text?
        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(host.getProject());

        List<Pair<PsiElement, TextRange>> injectedPsiFiles = injectedLanguageManager.getInjectedPsiFiles(host);
        if (injectedPsiFiles != null) {
            for (Pair<PsiElement, TextRange> psi_range : injectedPsiFiles) {
                //fixme check lastParent ?
                walkOn &= psi_range.first.processDeclarations(processor, state, lastParent, place);
            }
        }

        return walkOn;
    }
}
