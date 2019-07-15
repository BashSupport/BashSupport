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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.ResolveState;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * @author jansorg
 */
public class BashVarVariantsProcessor extends BashAbstractProcessor implements BashVarCollectorProcessor {
    private final List<BashVarDef> variables = Lists.newLinkedList();
    private final Set<String> variableNames = Sets.newHashSet();
    private final PsiElement startElement;
    private final boolean allowSameFile;
    private final boolean allowOtherFiles;
    private final PsiFile startFile;

    /**
     * @param startElement
     * @param allowSameFile   if ${@code true}, then results which are in the same file as startElement are kept
     * @param allowOtherFiles if ${@code true}, then results which are not in the same file as startElement are kept
     */
    public BashVarVariantsProcessor(PsiElement startElement, boolean allowSameFile, boolean allowOtherFiles) {
        super(false);

        this.startElement = startElement;
        this.allowSameFile = allowSameFile;
        this.allowOtherFiles = allowOtherFiles;

        this.startFile = startElement.getContainingFile();
    }

    public boolean execute(@NotNull PsiElement psiElement, @NotNull ResolveState resolveState) {
        ProgressManager.checkCanceled();

        if (startFile != null) {
            PsiFile file = psiElement.getContainingFile();
            if (!allowSameFile && startFile.isEquivalentTo(file)) {
                return true;
            }
            if (!allowOtherFiles && !startFile.isEquivalentTo(file)) {
                return true;
            }
        }

        if (psiElement instanceof BashVarDef) {
            BashVarDef varDef = (BashVarDef) psiElement;
            if (varDef.isStaticAssignmentWord()
                    && !varDef.isCommandLocal()
                    && !variableNames.contains(varDef.getName())
                    && BashVarUtils.isInDefinedScope(startElement, varDef)) {
                variables.add(varDef);
                variableNames.add(varDef.getName());
            }
        }

        return true;
    }

    public List<BashVarDef> getVariables() {
        return variables;
    }

    public <T> T getHint(@NotNull Key<T> tKey) {
        return null;
    }
}