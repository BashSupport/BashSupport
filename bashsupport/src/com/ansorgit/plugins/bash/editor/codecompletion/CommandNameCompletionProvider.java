package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashInternalCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashFunctionVariantsProcessor;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
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
class CommandNameCompletionProvider extends BashCompletionProvider {
    @Override
    void addTo(CompletionContributor contributor) {
        BashPsiPattern internal = new BashPsiPattern().withParent(BashInternalCommand.class);
        BashPsiPattern generic = new BashPsiPattern().withParent(BashGenericCommand.class);
        ElementPattern<PsiElement> internalOrGeneric = StandardPatterns.or(internal, generic);

        BashPsiPattern pattern = new BashPsiPattern().withParent(internalOrGeneric);

        contributor.extend(CompletionType.BASIC, pattern, this);
    }

    @Override
    protected void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
        //this is still required, although the test cases fail
        //to check run the plugin in IDEA and do a completion after a single $ character, this method should
        //not be invoked if working properly
        if (currentText.startsWith("$")) {
            return;
        }

        PsiElement element = parameters.getPosition();
        PsiElement originalElement = parameters.getOriginalPosition();

        if (element.getParent() != null && element.getParent().getParent() instanceof BashParameterExpansion) {
            return;
        }

        PsiElement lookupElement = originalElement != null ? originalElement : element;
        BashFunctionVariantsProcessor processor = new BashFunctionVariantsProcessor(lookupElement);
        PsiTreeUtil.treeWalkUp(processor, lookupElement, lookupElement.getContainingFile(), ResolveState.initial());

        Collection<LookupElement> functionItems = CompletionProviderUtils.createPsiItems(processor.getFunctionDefs());
        result.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.Function.ordinal(), functionItems));

        //offer predefined and built-ins on the second invocation count
        //if no local elements were found, offer the global at the first invocation, if enabled
        int invocationCount = parameters.getInvocationCount();
        if (invocationCount >= 2 || functionItems.isEmpty()) {
            Project project = lookupElement.getProject();

            //make sure to use the prefix text of the current lookup element, it might be a composed word
            //element and we need to match the commands with that prefix to avoid illegal suggestions
            String lookupPrefix = findCurrentText(parameters, lookupElement);
            CompletionResultSet commandResult = result.withPrefixMatcher(lookupPrefix);

            if (BashProjectSettings.storedSettings(project).isAutocompleteBuiltinCommands()) {
                Collection<LookupElement> globals = CompletionProviderUtils.createItems(LanguageBuiltins.commands, BashIcons.GLOBAL_VAR_ICON);
                commandResult.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.GlobalCommand.ordinal(), globals));
            }

            if (BashProjectSettings.storedSettings(project).isSupportBash4()) {
                Collection<LookupElement> globals = CompletionProviderUtils.createItems(LanguageBuiltins.commands_v4, BashIcons.GLOBAL_VAR_ICON);
                commandResult.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.GlobalCommand.ordinal(), globals));
            }
        } else {
            CompletionService.getCompletionService().setAdvertisementText("Press twice for built-in commands");
        }
    }
}
