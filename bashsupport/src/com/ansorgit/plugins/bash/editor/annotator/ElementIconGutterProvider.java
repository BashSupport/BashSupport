/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ElementIconGutterProvider.java, Class: ElementIconGutterProvider
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

package com.ansorgit.plugins.bash.editor.annotator;

import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Default gutter icon provider for psi elements.
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 8:13:21 PM
 */
class ElementIconGutterProvider extends GutterIconRenderer {
    private final PsiElement element;

    public ElementIconGutterProvider(PsiElement element) {
        this.element = element;
    }

    @NotNull
    @Override
    public Icon getIcon() {
        return element.getIcon(Iconable.ICON_FLAG_OPEN);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ElementIconGutterProvider)) {
            return false;
        }

        return this.element == ((ElementIconGutterProvider) o).element && element.equals(o);
    }

    @Override
    public int hashCode() {
        return element != null ? element.hashCode() : 0;
    }
}
