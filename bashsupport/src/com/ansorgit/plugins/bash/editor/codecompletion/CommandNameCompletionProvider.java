package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashFunctionVariantsProcessor;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
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
    protected void addBashCompletions(PsiElement element, String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        if (currentText.startsWith("$")) {
            return;
        }

        BashFunctionVariantsProcessor processor = new BashFunctionVariantsProcessor();
        PsiTreeUtil.treeWalkUp(processor, element, element.getContainingFile(), ResolveState.initial());

        Collection<LookupElement> functionItems = CompletionProviderUtils.createPsiItems(processor.getFunctionDefs());
        resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.Function.ordinal(), functionItems));

        if (BashProjectSettings.storedSettings(element.getProject()).isAutocompleteBuiltinCommands()) {
            Collection<LookupElement> globals = CompletionProviderUtils.createItems(LanguageBuiltins.commands, BashIcons.GLOBAL_VAR_ICON);
            resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.GlobalCommand.ordinal(), globals));
        }

        if (BashProjectSettings.storedSettings(element.getProject()).isSupportBash4()) {
            Collection<LookupElement> globals = CompletionProviderUtils.createItems(LanguageBuiltins.commands_v4, BashIcons.GLOBAL_VAR_ICON);
            resultWithoutPrefix.addAllElements(CompletionProviderUtils.wrapInGroup(CompletionGrouping.GlobalCommand.ordinal(), globals));
        }
    }
}
