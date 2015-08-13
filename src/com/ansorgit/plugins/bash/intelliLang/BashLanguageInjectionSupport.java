package com.ansorgit.plugins.bash.intelliLang;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.plugins.intelliLang.inject.AbstractLanguageInjectionSupport;
import org.intellij.plugins.intelliLang.inject.config.BaseInjection;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BashLanguageInjectionSupport extends AbstractLanguageInjectionSupport {

    private static final Class[] EMPTY = new Class[0];

    @Override
    public boolean isApplicableTo(PsiLanguageInjectionHost host) {
        return host instanceof BashPsiElement;
    }

    @Override
    public boolean removeInjection(PsiElement psiElement) {
        return super.removeInjection(psiElement);
    }

    @Override
    public BaseInjection createInjection(Element element) {
        return super.createInjection(element);
    }

    @Nullable
    @Override
    public BaseInjection findCommentInjection(@NotNull PsiElement host, Ref<PsiElement> commentRef) {
        return super.findCommentInjection(host, commentRef);
    }

    @NotNull
    @Override
    public String getId() {
        return "BashLangInjection";
    }

    @NotNull
    @Override
    public Class[] getPatternClasses() {
        return EMPTY;
    }
}
