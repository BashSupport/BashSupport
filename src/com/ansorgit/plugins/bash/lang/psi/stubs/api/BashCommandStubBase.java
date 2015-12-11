package com.ansorgit.plugins.bash.lang.psi.stubs.api;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StubElement;

/**
 * @author jansorg
 */
public interface BashCommandStubBase<T extends PsiElement> extends StubElement<T> {
    boolean isInternalCommand(boolean bash4);

    boolean isGenericCommand();
}
