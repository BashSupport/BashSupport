package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class BashLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof BashCharSequence && host.isValidHost()) {
            BashCharSequence string = (BashCharSequence) host;
            injectionPlacesRegistrar.addPlace(BashFileType.BASH_LANGUAGE, string.getTextContentRange(), null, null);
        }
    }
}
