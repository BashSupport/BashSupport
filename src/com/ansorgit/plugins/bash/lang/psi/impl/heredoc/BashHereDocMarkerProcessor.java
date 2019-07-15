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

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;

/**
 * PSI tree processor which collects heredoc markers in the tree.
 * <br>
 * @author jansorg
 */
class BashHereDocMarkerProcessor extends BashAbstractProcessor {
    private final String referencedName;
    private final Class<? extends BashHereDocMarker> otherEndsType;

    public BashHereDocMarkerProcessor(String referencedName, Class<? extends BashHereDocMarker> otherEndsType) {
        super(true);

        this.referencedName = referencedName;
        this.otherEndsType = otherEndsType;
    }

    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
        boolean isValid = otherEndsType.isInstance(element) && referencedName.equals(((BashHereDocMarker) element).getMarkerText());
        if (isValid) {
            storeResult(element, 100, null);
            return false;
        }

        return true;
    }

    public <T> T getHint(@NotNull Key<T> hintKey) {
        return null;
    }
}
