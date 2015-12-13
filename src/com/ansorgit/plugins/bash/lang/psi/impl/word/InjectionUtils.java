package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarUse;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
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

    static List<BashVarUse> collectVariableUses(BashLanguageInjectionHost host) {
        List<Pair<PsiElement, TextRange>> injectedPsiFiles = InjectedLanguageManager.getInstance(host.getProject()).getInjectedPsiFiles(host);
        if (injectedPsiFiles == null || injectedPsiFiles.isEmpty()) {
            return Collections.emptyList();
        }

        final List<BashVarUse> variables = Lists.newLinkedList();
        for (Pair<PsiElement, TextRange> psiFilePair : injectedPsiFiles) {
            BashPsiUtils.visitRecursively(psiFilePair.first, new BashVisitor() {
                @Override
                public void visitVarUse(BashVar var) {
                    variables.add((BashVarUse) var);
                }
            });
        }

        return variables;
    }

    static List<BashVarDef> collectVariableDefinitions(BashLanguageInjectionHost host) {
        List<Pair<PsiElement, TextRange>> injectedPsiFiles = InjectedLanguageManager.getInstance(host.getProject()).getInjectedPsiFiles(host);
        if (injectedPsiFiles == null || injectedPsiFiles.isEmpty()) {
            return Collections.emptyList();
        }

        final List<BashVarDef> variables = Lists.newLinkedList();
        for (Pair<PsiElement, TextRange> psiFilePair : injectedPsiFiles) {
            BashPsiUtils.visitRecursively(psiFilePair.first, new BashVisitor() {
                @Override
                public void visitVarDef(BashVarDef varDef) {
                    variables.add(varDef);
                }
            });
        }

        return variables;
    }
}
