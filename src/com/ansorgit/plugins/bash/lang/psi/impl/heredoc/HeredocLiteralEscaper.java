package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

/**
 */
public class HeredocLiteralEscaper<T extends PsiLanguageInjectionHost> extends LiteralTextEscaper<T> {
    public HeredocLiteralEscaper(T host) {
        super(host);
    }

    @Override
    public boolean decode(@NotNull final TextRange rangeInsideHost, @NotNull StringBuilder outChars) {
        ProperTextRange.assertProperRange(rangeInsideHost);

        outChars.append(rangeInsideHost.substring(myHost.getText()));

        return true;
    }

    @Override
    public int getOffsetInHost(int offsetInDecoded, @NotNull final TextRange rangeInsideHost) {
        return offsetInDecoded + rangeInsideHost.getStartOffset();
    }

    @Override
    public boolean isOneLine() {
        return false;
    }
}