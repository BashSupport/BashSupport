/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: PathLookupElement.java, Class: PathLookupElement
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

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.openapi.util.IconLoader;
import com.intellij.util.Icons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Lookup element which contains a simple path element.
 * The icon is shown according to the type (file / directory entry).
 * <p/>
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:52:31 PM
 */
final class PathLookupElement extends LookupElement {
    private static final Icon textIcon = IconLoader.getIcon("/fileTypes/text.png");

    private final String path;
    private final boolean isFile;

    public PathLookupElement(String path, boolean isFile) {
        this.path = path;
        this.isFile = isFile;
    }

    @Override
    public void renderElement(LookupElementPresentation presentation) {
        super.renderElement(presentation);
        presentation.setIcon(isFile ? textIcon : Icons.DIRECTORY_CLOSED_ICON);
    }

    @NotNull
    @Override
    public String getLookupString() {
        return path;
    }
}
