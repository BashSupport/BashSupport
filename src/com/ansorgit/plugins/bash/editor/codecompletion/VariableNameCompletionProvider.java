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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarCollectorProcessor;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarVariantsProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;

import java.util.Collection;

/**
 * Completion provider for variable names.
 */
class VariableNameCompletionProvider extends AbstractBashCompletionProvider {
    @Override
    void addTo(CompletionContributor contributor) {
        BashPsiPattern insideVar = new BashPsiPattern().withParent(BashVar.class);

        contributor.extend(CompletionType.BASIC, insideVar, this);
    }

    @Override
    protected void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
        PsiElement element = parameters.getPosition();

        BashVar varElement = PsiTreeUtil.getContextOfType(element, BashVar.class, false);
        boolean dollarPrefix = currentText != null && currentText.startsWith("$");
        boolean insideExpansion = element.getParent() != null && element.getParent().getParent() instanceof BashParameterExpansion;
        if (varElement == null && !dollarPrefix && !insideExpansion) {
            return;
        }

        int invocationCount = parameters.getInvocationCount();
        int resultLength = 0;

        PsiElement original = parameters.getOriginalPosition();
        BashVar varElementOriginal = original != null ? PsiTreeUtil.getContextOfType(original, BashVar.class, false) : null;

        if (varElement != null) {
            // only keep vars of included files when starting in the original file
            PsiElement originalRef = varElementOriginal != null ? varElementOriginal : original;
            if (originalRef != null) {
                resultLength += addCollectedVariables(original, result, new BashVarVariantsProcessor(originalRef, false, true));
            }

            // only keep vars of the dummy file when starting in the dummy file
            resultLength += addCollectedVariables(element, result, new BashVarVariantsProcessor(varElement, true, false));
        } else {
            // not in a variable element, but collect all known variable names at this offset in the current file
            if (original != null) {
                resultLength += addCollectedVariables(original, result, new BashVarVariantsProcessor(original, false, true));
            }
            resultLength += addCollectedVariables(element, result, new BashVarVariantsProcessor(element, false, true));
        }

        if (currentText != null && (dollarPrefix || insideExpansion) && (invocationCount >= 2 || resultLength == 0)) {
            Project project = element.getProject();
            addBuiltInVariables(result, project);
            addGlobalVariables(result, project);
        } else {
            result.addLookupAdvertisement("Press twice for global variables");
        }
    }

    private int addCollectedVariables(PsiElement element, CompletionResultSet result, BashVarCollectorProcessor processor) {
        PsiTreeUtil.treeWalkUp(processor, element, BashPsiUtils.findFileContext(element), ResolveState.initial());

        Collection<LookupElement> items = CompletionProviderUtils.createFromPsiItems(processor.getVariables(), BashIcons.VAR_ICON, CompletionGrouping.NormalVar.ordinal());
        result.addAllElements(items);

        return items.size();
    }

    private void addGlobalVariables(CompletionResultSet result, Project project) {
        if (BashProjectSettings.storedSettings(project).isAutcompleteGlobalVars()) {
            Collection<LookupElement> globalVars = CompletionProviderUtils.createItems(BashProjectSettings.storedSettings(project).getGlobalVariables(), BashIcons.GLOBAL_VAR_ICON, true, CompletionGrouping.GlobalVar.ordinal());
            result.addAllElements(globalVars);
        }
    }

    private void addBuiltInVariables(CompletionResultSet result, Project project) {
        if (BashProjectSettings.storedSettings(project).isAutocompleteBuiltinVars()) {
            Collection<LookupElement> shellBuiltIns = CompletionProviderUtils.createItems(LanguageBuiltins.bashShellVars, BashIcons.BASH_VAR_ICON, true, CompletionGrouping.BuiltInVar.ordinal());
            result.addAllElements(shellBuiltIns);

            Collection<LookupElement> bashBuiltIns = CompletionProviderUtils.createItems(LanguageBuiltins.bourneShellVars, BashIcons.BOURNE_VAR_ICON, true, CompletionGrouping.BuiltInVar.ordinal());
            result.addAllElements(bashBuiltIns);
        }
    }
}
