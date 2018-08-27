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

import com.ansorgit.plugins.bash.util.OSUtil;
import com.intellij.codeInsight.completion.PrioritizedLookupElement;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.psi.PsiNamedElement;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jansorg
 */
final class CompletionProviderUtils {
    private static final Icon fileIcon = IconLoader.getIcon("/fileTypes/text.png");
    private static final Icon dirIcon = PlatformIcons.DIRECTORY_CLOSED_ICON;

    private CompletionProviderUtils() {
    }

    static Collection<LookupElement> createFromPsiItems(Collection<? extends PsiNamedElement> elements, @Nullable Icon icon, @Nullable Integer groupId) {
        return elements.stream().map(psi -> {
            LookupElementBuilder element = LookupElementBuilder.create(psi).withCaseSensitivity(true);
            if (icon != null) {
                element = element.withIcon(icon);
            }
            if (groupId != null) {
                return PrioritizedLookupElement.withGrouping(element, groupId);
            }
            return element;
        }).collect(Collectors.toList());
    }

    static Collection<LookupElement> createItems(Collection<String> lookupStrings, final Icon icon, boolean trimLookupString, Integer groupId) {
        return lookupStrings
                .stream()
                .map(item -> {
                    LookupElementBuilder elementBuilder = LookupElementBuilder.create(item).withCaseSensitivity(true);
                    if (icon != null) {
                        elementBuilder = elementBuilder.withIcon(icon);
                    }

                    if (trimLookupString) {
                        elementBuilder = elementBuilder.withLookupString(item.replace("_", ""));
                    }

                    if (groupId != null) {
                        return PrioritizedLookupElement.withGrouping(elementBuilder, groupId);
                    }
                    return elementBuilder;
                })
                .collect(Collectors.toList());
    }

    static Collection<LookupElement> createPathItems(List<String> osPathes) {
        return osPathes.stream()
                .map(path ->
                        //fix the windows file and directory pathes to be cygwin compatible
                        SystemInfoRt.isWindows ? OSUtil.toBashCompatible(path) : path
                )
                .map(path -> {
                    int groupId = path.startsWith("/") ? CompletionGrouping.AbsoluteFilePath.ordinal() : CompletionGrouping.RelativeFilePath.ordinal();
                    return PrioritizedLookupElement.withGrouping(createPathLookupElement(path, !path.endsWith("/")), groupId);
                }).collect(Collectors.toList());
    }

    static LookupElement createPathLookupElement(String path, boolean isFile) {
        return LookupElementBuilder.create(path).withIcon(isFile ? fileIcon : dirIcon);
    }
}
