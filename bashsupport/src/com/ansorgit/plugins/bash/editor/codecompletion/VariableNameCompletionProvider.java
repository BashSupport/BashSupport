package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarCollectorProcessor;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarVariantsProcessor;
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
 * User: jansorg
 * Date: 07.02.11
 * Time: 18:28
 */
class VariableNameCompletionProvider extends BashCompletionProvider {
    @Override
    void addTo(CompletionContributor contributor) {
        BashPsiPattern insideVar = new BashPsiPattern().inside(BashVar.class);
        BashPsiPattern afterDollar = new BashPsiPattern().withText("$");

        contributor.extend(CompletionType.BASIC, insideVar, this);
        contributor.extend(CompletionType.BASIC, afterDollar, this);
    }

    @Override
    protected void addBashCompletions(PsiElement element, String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        BashVar varElement = PsiTreeUtil.getContextOfType(element, BashVar.class);
        boolean dollarPrefix = currentText != null && currentText.startsWith("$");

        if (varElement == null && !dollarPrefix) {
            return;
        }

        if (currentText != null && dollarPrefix) {
            Project project = element.getProject();
            addBuildInVariables(resultWithoutPrefix, project);
            addGlobalVariables(resultWithoutPrefix, project);
        }

        if (varElement != null) {
            addCollectedVariables(element, resultWithoutPrefix, new BashVarVariantsProcessor(varElement));
        } else {
            //not in a variable element, but collect all known variable names at this offset in the current file
            addCollectedVariables(element, resultWithoutPrefix, new BashVarVariantsProcessor(element));
        }
    }

    private void addCollectedVariables(PsiElement element, CompletionResultSet resultWithoutPrefix, BashVarCollectorProcessor processor) {
        PsiTreeUtil.treeWalkUp(processor, element, element.getContainingFile(), ResolveState.initial());

        Collection<LookupElement> items = CompletionProviderUtils.createPsiItems(processor.getVariables());
        resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.NormalVar.ordinal(), items));
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
