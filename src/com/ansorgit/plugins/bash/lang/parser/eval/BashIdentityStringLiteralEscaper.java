/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.openapi.util.ProperTextRange;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

/**
 * Based on the original Jetbrains StringLiteralEscaper code.
 */
public class BashIdentityStringLiteralEscaper<T extends PsiLanguageInjectionHost> extends LiteralTextEscaper<T> {
    public BashIdentityStringLiteralEscaper(T host) {
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
        return true;
    }
}