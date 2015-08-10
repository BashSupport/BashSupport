package com.ansorgit.plugins.bash.lang.psi.api.shell;

import com.ansorgit.plugins.bash.lang.psi.api.BashKeyword;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Defines the PSI interface for the trap command.
 */
public interface BashTrapCommand extends PsiElement, BashKeyword {
    /**
     *
     * @return The element representing the signal handler
     */
    @Nullable
    PsiElement getSignalHandlerElement();

    List<PsiElement> getSignalSpec();
}
