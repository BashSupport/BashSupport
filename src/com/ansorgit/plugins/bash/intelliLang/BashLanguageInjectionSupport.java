package com.ansorgit.plugins.bash.intelliLang;

import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;

public class BashLanguageInjectionSupport extends AbstractLanguageInjectionSupport {

    private static final Class[] EMPTY = new Class[0];

    @Override
    public boolean isApplicableTo(PsiLanguageInjectionHost host) {
        if (host instanceof BashLanguageInjectionHost) {
            return !((BashLanguageInjectionHost) host).isValidBashLanguageHost();
        }

        return host instanceof BashPsiElement;
    }

    @NotNull
    @Override
    public String getId() {
        return "BashInjection";
    }

    @NotNull
    @Override
    public Class[] getPatternClasses() {
        return EMPTY;
    }
}
