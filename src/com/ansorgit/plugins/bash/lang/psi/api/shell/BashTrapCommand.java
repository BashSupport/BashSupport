package com.ansorgit.plugins.bash.lang.psi.api.shell;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Defines the PSI interface for the trap command.
 */
public interface BashTrapCommand extends BashKeyword {
    PsiElement getCommandElement();

    List<PsiElement> getSignalSpec();
}
