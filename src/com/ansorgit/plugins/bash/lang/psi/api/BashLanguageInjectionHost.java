package com.ansorgit.plugins.bash.lang.psi.api;

/**
 * Marks elements which may provide language injection for the Bash language.
 */
public interface BashLanguageInjectionHost extends BashCharSequence {
    boolean isValidBashLanguageHost();
}
