/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLineMarkerProvider.java, Class: BashLineMarkerProvider
 * Last modified: 2013-01-25
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

package com.ansorgit.plugins.bash.editor;

import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.codeHighlighting.Pass;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.util.PlatformIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * Provides Bash line markers for a given element.
 *
 * @author Joachim Ansorg
 */
public class BashLineMarkerProvider implements com.intellij.codeInsight.daemon.LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement element) {
        if (element instanceof BashFunctionDefName && element.getParent() instanceof BashFunctionDef) {
            return new LineMarkerInfo<BashFunctionDefName>((BashFunctionDefName) element, element.getTextRange(), PlatformIcons.METHOD_ICON, Pass.UPDATE_ALL, null, null, GutterIconRenderer.Alignment.LEFT);
        }

        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> elements, @NotNull Collection<LineMarkerInfo> result) {

    }
}
