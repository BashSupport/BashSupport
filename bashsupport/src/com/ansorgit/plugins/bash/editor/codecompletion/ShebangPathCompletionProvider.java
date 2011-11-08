/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangPathCompletionProvider.java, Class: ShebangPathCompletionProvider
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

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.google.common.base.Predicate;
import com.intellij.codeInsight.completion.CompletionContributor;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionType;
import com.intellij.psi.PsiElement;

import java.io.File;

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
    void addTo(CompletionContributor contributor) {
        contributor.extend(CompletionType.BASIC, new BashPsiPattern().inside(BashShebang.class), this);
    }

    @Override
    protected Predicate<File> createFileFilter() {
        return new Predicate<File>() {
            public boolean apply(File file) {
                return file.canExecute() && file.canRead();
            }
        };
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
        PsiElement command = element;
        while (command != null && !(command instanceof BashShebang)) {
            command = command.getParent();
        }

        if (command != null) {
            BashShebang shebang = (BashShebang) command;
            String shellcommand = shebang.shellCommand();

            int elementOffset = parameters.getOffset() - shebang.commandRange().getStartOffset();
            return (elementOffset > 0 && elementOffset <= shellcommand.length())
                    ? shellcommand.substring(0, elementOffset)
                    : null;
        }

        return super.findCurrentText(parameters, element);
    }
}