package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarCollectorProcessor;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarVariantsProcessor;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;

import java.util.Collection;

/**
 * User: jansorg
 * Date: 07.02.11
 * Time: 18:28
 */
class VariableNameCompletionProvider extends BashCompletionProvider {
    @Override
    void addTo(CompletionContributor contributor) {
        BashPsiPattern insideVar = new BashPsiPattern().withParent(BashVar.class);

        contributor.extend(CompletionType.BASIC, insideVar, this);
        //contributor.extend(CompletionType.BASIC, afterDollar, this);
    }

    @Override
    protected void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        PsiElement element = parameters.getPosition();
        PsiElement original = parameters.getOriginalPosition();

        BashVar varElement = PsiTreeUtil.getContextOfType(original, BashVar.class);
        boolean dollarPrefix = currentText != null && currentText.startsWith("$");

        boolean insideExpansion = element.getParent() != null && element.getParent().getParent() instanceof BashParameterExpansion;

        if (varElement == null && !dollarPrefix && !insideExpansion) {
            return;
        }

        int invocationCount = parameters.getInvocationCount();
        int resultLength = 0;

        //fixme Currently we only look into the current file if no original element is given, better: we should collect locals and the included vars from the original file

        if (varElement != null) {
            resultLength += addCollectedVariables(original, resultWithoutPrefix, new BashVarVariantsProcessor(varElement));
        } else {
            //not in a variable element, but collect all known variable names at this offset in the current file
            PsiElement lookupElement = original != null ? original : element;

            resultLength += addCollectedVariables(lookupElement, resultWithoutPrefix, new BashVarVariantsProcessor(lookupElement));
        }

        if (currentText != null && dollarPrefix && (invocationCount >= 2 || resultLength == 0)) {
            Project project = element.getProject();
            addBuildInVariables(resultWithoutPrefix, project);
            addGlobalVariables(resultWithoutPrefix, project);
        } else {
            CompletionService.getCompletionService().setAdvertisementText("Press twice for global variables");
        }
    }

    private int addCollectedVariables(PsiElement element, CompletionResultSet resultWithoutPrefix, BashVarCollectorProcessor processor) {
        PsiTreeUtil.treeWalkUp(processor, element, element.getContainingFile(), ResolveState.initial());

        Collection<LookupElement> items = CompletionProviderUtils.createPsiItems(processor.getVariables());
        resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.NormalVar.ordinal(), items));

        return items.size();
    }

    private void addGlobalVariables(CompletionResultSet resultWithoutPrefix, Project project) {
        if (BashProjectSettings.storedSettings(project).isAutcompleteGlobalVars()) {
            Collection<LookupElement> globalVars = CompletionProviderUtils.createItems(BashProjectSettings.storedSettings(project).getGlobalVariables(), BashIcons.GLOBAL_VAR_ICON);
            resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.GlobalVar.ordinal(), globalVars));
        }
    }

    private void addBuildInVariables(CompletionResultSet result, Project project) {
        if (BashProjectSettings.storedSettings(project).isAutocompleteBuiltinVars()) {
            Collection<LookupElement> shellBuiltIns = CompletionProviderUtils.createItems(LanguageBuiltins.bashShellVars, BashIcons.BASH_VAR_ICON);
            result.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.BuiltInVar.ordinal(), shellBuiltIns));

            Collection<LookupElement> bashBuiltIns = CompletionProviderUtils.createItems(LanguageBuiltins.bourneShellVars, BashIcons.BOURNE_VAR_ICON);
            result.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.BuiltInVar.ordinal(), bashBuiltIns));
        }
    }
}
