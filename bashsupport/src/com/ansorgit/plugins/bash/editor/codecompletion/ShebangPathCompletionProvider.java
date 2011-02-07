/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangPathCompletionProvider.java, Class: ShebangPathCompletionProvider
 * Last modified: 2010-03-28
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

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.psi.PsiElement;

/**
 * This completion provider provides code completion for file / directory paths in the file.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:27:52 PM
 */
class ShebangPathCompletionProvider extends AbsolutePathCompletionProvider {
    public ShebangPathCompletionProvider() {
    }

    @Override
    protected PsiElement findElement(PsiElement element) {
        if (element instanceof BashShebang) {
            return element;
        }

        if (element.getParent() != null) {
            return findElement(element.getParent());
        }

        return null;
    }

    @Override
    protected String findOriginalText(PsiElement element) {
        String original = element.getText();

        if (element instanceof BashShebang) {
            int offset = ((BashShebang) element).getShellCommandOffset();
            return original.substring(offset);
        }

        return original;
    }

    @Override
    protected String findCurrentText(CompletionParameters parameters, PsiElement element) {
        BashShebang shebang = (BashShebang) element;
        String shellcommand = shebang.shellCommand();

        int elementOffset = parameters.getOffset() - shebang.commandRange().getStartOffset();
        return (elementOffset > 0 && elementOffset <= shellcommand.length())
                ? shellcommand.substring(0, elementOffset)
                : null;
    }
}