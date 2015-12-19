package com.ansorgit.plugins.bash.intelliLang;

import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport;
import org.jetbrains.annotations.NotNull;

public class BashLanguageInjectionSupport extends AbstractLanguageInjectionSupport {

    private static final Class[] EMPTY = new Class[0];

    @Override
    public boolean isApplicableTo(PsiLanguageInjectionHost host) {
        return host instanceof BashWord || host instanceof BashString;
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
