/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: DynamicPathCompletionProvider.java, Class: DynamicPathCompletionProvider
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.util.CompletionUtil;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

/**
 * Completion provider which provides completion even if the current path contains dynamic
 * parts like $HOME or ~ .
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 10:32:06 PM
 */
class DynamicPathCompletionProvider extends BashCompletionProvider {
    private static final Set<String> homePrefixes = Sets.newHashSet("$HOME", "~");
    private static final Set<String> supportedPrefixes = Sets.newHashSet("$HOME", "~", ".");

    public DynamicPathCompletionProvider() {
    }

    @Override
    void addTo(CompletionContributor contributor) {
        contributor.extend(CompletionType.BASIC, new BashPsiPattern().withParent(BashWord.class), this);
    }

    @Override
    protected void addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
        //if we are in a combined word, get it
        PsiElement parentElement = parameters.getPosition().getParent();
        if (parentElement instanceof BashWord) {
            currentText = findCurrentText(parameters, parentElement);
        }

        result = result.withPrefixMatcher(currentText);

        String usedPrefix = findUsedPrefix(currentText);
        if (usedPrefix == null) {
            return;
        }

        //fixme shouldn't be needed
        String baseDir = findBaseDir(parameters, usedPrefix);
        if (baseDir == null) {
            return;
        }

        String relativePath = currentText.substring(usedPrefix.length());
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }

        List<String> completions = CompletionUtil.completeRelativePath(baseDir, usedPrefix, relativePath);
        result.addAllElements(CompletionProviderUtils.createPathItems(completions));
    }

    @Nullable
    private String findBaseDir(CompletionParameters parameters, String usedPrefix) {
        if (homePrefixes.contains(usedPrefix)) {
            return System.getenv("HOME");
        }

        PsiDirectory file = parameters.getOriginalFile().getParent();
        return file != null ? file.getVirtualFile().getPath() : null;
    }

    private String findUsedPrefix(String originalText) {
        String usedPrefix = null;
        for (String prefix : supportedPrefixes) {
            if (originalText.startsWith(prefix)) {
                usedPrefix = prefix;
                break;
            }
        }

        return usedPrefix;
    }
}
