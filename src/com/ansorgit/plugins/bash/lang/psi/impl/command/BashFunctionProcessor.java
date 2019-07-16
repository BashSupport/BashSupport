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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.impl.Keys;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Sets;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class BashFunctionProcessor extends BashAbstractProcessor {
    private final String symboleName;
    private final boolean ignoreExecuteResult;
    private Set<PsiElement> visitedElements = Sets.newIdentityHashSet();

    public BashFunctionProcessor(String symboleName) {
        this(symboleName, false);
    }

    public BashFunctionProcessor(String symboleName, boolean ignoreExecuteResult) {
        super(true);

        this.symboleName = symboleName;
        this.ignoreExecuteResult = ignoreExecuteResult;
    }

    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState resolveState) {
        ProgressManager.checkCanceled();

        if (element instanceof BashFunctionDef) {
            BashFunctionDef funcDef = (BashFunctionDef) element;

            if (symboleName.equals(funcDef.getName())) {
                storeResult(element, BashPsiUtils.blockNestingLevel(funcDef), null);
                return ignoreExecuteResult;
            }
        }

        return true;
    }

    public <T> T getHint(@NotNull Key<T> key) {
        if (key.equals(Keys.VISITED_SCOPES_KEY)) {
            return (T) visitedElements;
        }

        if (key.equals(Keys.FILE_WALK_GO_DEEP)) {
            return (T) Boolean.FALSE;
        }

        return null;
    }
}
