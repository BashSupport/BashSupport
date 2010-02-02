/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: PathLookupElement.java, Class: PathLookupElement
 * Last modified: 2009-12-04
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
import org.jetbrains.annotations.NotNull;

/**
 * Lookup element which contains a simple path element.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:52:31 PM
 */
public final class PathLookupElement extends LookupElement {
    private final String path;

    public PathLookupElement(String path) {
        this.path = path;
    }

    @NotNull
    @Override
    public String getLookupString() {
        return path;
    }
}
