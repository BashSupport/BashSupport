/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctionProcessor.java, Class: BashFunctionProcessor
 * Last modified: 2010-06-30
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;

/**
 * Date: 12.04.2009
 * Time: 23:20:07
 *
 * @author Joachim Ansorg
 */
public class BashFunctionProcessor extends BashAbstractProcessor {
    private final String symboleName;
    private final boolean ignoreExecuteResult;

    public BashFunctionProcessor(String symboleName) {
        this(symboleName, false);
    }

    public BashFunctionProcessor(String symboleName, boolean ignoreExecuteResult) {
        this.symboleName = symboleName;
        this.ignoreExecuteResult = ignoreExecuteResult;
    }

    public boolean execute(PsiElement element, ResolveState resolveState) {
        if (element instanceof BashFunctionDef) {
            final BashFunctionDef f = (BashFunctionDef) element;

            if (symboleName.equals(f.getName())) {
                storeResult(element, BashPsiUtils.blockNestingLevel(f));
                return ignoreExecuteResult ? true : false;
            }
        }

        return true;
    }

    public <T> T getHint(Key<T> tKey) {
        return null;
    }
}
