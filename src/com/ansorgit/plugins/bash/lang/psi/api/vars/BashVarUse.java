package com.ansorgit.plugins.bash.lang.psi.api.vars;

import com.intellij.psi.PsiNamedElement;

public interface BashVarUse extends PsiNamedElement {
    boolean isSingleWord();
}
