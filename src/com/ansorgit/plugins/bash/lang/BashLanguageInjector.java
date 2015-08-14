package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashLanguageInjectionHost;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class BashLanguageInjector implements LanguageInjector {
    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (host instanceof BashLanguageInjectionHost && ((BashLanguageInjectionHost) host).isValidBashLanguageHost()) {
            BashLanguageInjectionHost string = (BashLanguageInjectionHost) host;
            injectionPlacesRegistrar.addPlace(BashFileType.BASH_LANGUAGE, string.getTextContentRange(), null, null);
        }
    }
}
