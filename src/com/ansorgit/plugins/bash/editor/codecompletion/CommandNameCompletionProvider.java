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
import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashInternalCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.impl.command.BashFunctionVariantsProcessor;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.ansorgit.plugins.bash.util.BashIcons;
import com.google.common.collect.Lists;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.patterns.ElementPattern;
import com.intellij.patterns.StandardPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides command completion.
 * <p>
 * Adds the system's binaries to the result with prefix matching first.
 * On 2nd invocation it adds all commands to the result to allow substring matching and not just prefix matching.
 *
 * @author jansorg
 */
class CommandNameCompletionProvider extends AbstractBashCompletionProvider {
    private final BashPathCompletionService completionService;

    public CommandNameCompletionProvider(BashPathCompletionService completionService) {
        this.completionService = completionService;
    }

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
        // this is still required, although the test cases fail
        // to check run the plugin in IDEA and do a completion after a single $ character, this method should
        // not be invoked if working properly
        if (currentText.startsWith("$")) {
            return;
        }

        PsiElement element = parameters.getPosition();
        if (element.getParent() != null && element.getParent().getParent() instanceof BashParameterExpansion) {
            return;
        }

        int addedItems = 0;

        Collection<LookupElement> functions = collectFunctions(element, CompletionGrouping.Function.ordinal());
        result.addAllElements(functions);
        addedItems += functions.size();

        int invocationCount = parameters.getInvocationCount();

        //make sure to use the prefix text of the current lookup element, it might be a composed word
        //element and we need to match the commands with that prefix to avoid illegal suggestions
        String lookupPrefix = findCurrentText(parameters, element);
        CompletionResultSet prefixedCommand = result.withPrefixMatcher(lookupPrefix);

        BashProjectSettings settings = BashProjectSettings.storedSettings(element.getProject());
        if (settings.isAutocompleteBuiltinCommands() && (invocationCount > 1 || addedItems == 0)) {
            Collection<LookupElement> commands = CompletionProviderUtils.createItems(LanguageBuiltins.commands, BashIcons.GLOBAL_VAR_ICON, true, CompletionGrouping.GlobalCommand.ordinal());
            addedItems += commands.size();

            prefixedCommand.addAllElements(commands);

            if (settings.isSupportBash4()) {
                commands = CompletionProviderUtils.createItems(LanguageBuiltins.commands_v4, BashIcons.GLOBAL_VAR_ICON, true, CompletionGrouping.GlobalCommand.ordinal());
                addedItems += commands.size();

                prefixedCommand.addAllElements(commands);
            }
        }

        if (settings.isAutocompletePathCommands()) {
            List<LookupElement> commands = collectPathCommands(currentText, invocationCount);
            addedItems += commands.size();

            prefixedCommand.addAllElements(commands);
        }

        // offer predefined and built-ins on the second invocation count
        // if no local elements were found, offer the global at the first invocation, if enabled
        if (invocationCount < 2 && addedItems == 0) {
            result.addLookupAdvertisement("Press twice for all built-in and system-wide commands");
        }
    }

    private List<LookupElement> collectPathCommands(String currentText, int invocationCount) {
        if (invocationCount < 2) {
            return Collections.emptyList();
        }

        //complete the current input with the executables found in $PATH
        Collection<BashPathCompletionService.CompletionItem> commands = invocationCount == 2
                ? completionService.findCommands(currentText)
                : completionService.allCommands();

        return commands.stream()
                .map(completionItem -> LookupElementBuilder.create(completionItem.getFilename())
                        .withCaseSensitivity(!SystemInfo.isWindows)
                        .withTypeText(completionItem.getPath(), true))
                .collect(Collectors.toCollection(Lists::newLinkedList));
    }

    @NotNull
    private Collection<LookupElement> collectFunctions(PsiElement lookupElement, int groupId) {
        BashFunctionVariantsProcessor processor = new BashFunctionVariantsProcessor(lookupElement);
        PsiTreeUtil.treeWalkUp(processor, lookupElement, BashPsiUtils.findFileContext(lookupElement), ResolveState.initial());

        return CompletionProviderUtils.createFromPsiItems(processor.getFunctionDefs(), BashIcons.FUNCTION_ICON, groupId);
    }
}
