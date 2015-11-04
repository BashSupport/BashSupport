package com.ansorgit.plugins.bash.editor;

import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;

public class SpellcheckerSupport extends SpellcheckingStrategy {
    @Override
    public boolean isMyContext(PsiElement element) {
        return true;
    }

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        return super.getTokenizer(element);
    }
}
