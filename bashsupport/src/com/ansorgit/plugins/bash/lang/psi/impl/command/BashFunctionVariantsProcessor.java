/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctionVariantsProcessor.java, Class: BashFunctionVariantsProcessor
 * Last modified: 2010-07-12
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
import com.google.common.collect.Lists;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;

import java.util.List;

/**
 * Date: 12.04.2009
 * Time: 23:20:07
 *
 * @author Joachim Ansorg
 */
public class BashFunctionVariantsProcessor extends BashAbstractProcessor {
    private final List<BashFunctionDef> functionDefs = Lists.newArrayList();
    private PsiElement startElement;

    public BashFunctionVariantsProcessor(PsiElement startElement) {
        super(true);
        this.startElement = startElement;
    }

    public boolean execute(PsiElement element, ResolveState resolveState) {
        if (element instanceof BashFunctionDef) {
            final BashFunctionDef f = (BashFunctionDef) element;

            if (BashPsiUtils.isValidReferenceScope(startElement, element)) {
                functionDefs.add(f);
            }
        }

        return true;
    }

    public <T> T getHint(Key<T> tKey) {
        return null;
    }

    public List<BashFunctionDef> getFunctionDefs() {
        return functionDefs;
    }
}