/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: DynamicPathCompletionProvider.java, Class: DynamicPathCompletionProvider
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.ansorgit.plugins.bash.util.CompletionUtil;
import com.google.common.collect.Sets;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
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
    private static final Set<String> supportedPrefixes = Sets.newHashSet("$HOME", "~", ".");
    private static final Set<String> homePrefixes = Sets.newHashSet("$HOME", "~");

    public DynamicPathCompletionProvider() {
    }

    @Override
    protected void addBashCompletions(PsiElement element, String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        String usedPrefix = findUsedPrefix(currentText);
        if (usedPrefix == null) {
            return;
        }

        String baseDir = findBaseDir(parameters, usedPrefix);
        if (baseDir == null) {
            return;
        }

        List<String> completions = CompletionUtil.completeRelativePath(baseDir, usedPrefix, currentText.substring(usedPrefix.length()));
        resultWithoutPrefix.addAllElements(CompletionProviderUtils.createPathItems(completions));
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
