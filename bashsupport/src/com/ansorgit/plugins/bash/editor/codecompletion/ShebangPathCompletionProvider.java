/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangPathCompletionProvider.java, Class: ShebangPathCompletionProvider
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

import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.util.CompletionUtil;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;

import java.util.List;

/**
 * This completion provider provides code completion for file / directory paths in the file.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:27:52 PM
 */
public class ShebangPathCompletionProvider extends BashCompletionProvider {
    private static final Logger log = Logger.getInstance("AbsolutePathCompletionProvider");

    public ShebangPathCompletionProvider() {
        super(true);
    }

    @Override
    protected List<String> addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        return CompletionUtil.completeAbsolutePath(currentText);
    }

    @Override
    protected PsiElement findElement(PsiElement element) {
        if (!(element instanceof BashShebang) && (element.getParent() instanceof BashShebang)) {
            return element.getParent();
        }

        if (element instanceof BashShebang) {
            return element;
        }

        return null;
    }

    @Override
    protected String findCurrentText(CompletionParameters parameters, PsiElement element) {
        BashShebang shebang = (BashShebang) element;
        String shellcommand = shebang.shellCommand();

        int elementOffset = parameters.getOffset() - shebang.shellCommandOffset();
        return elementOffset > 0 && elementOffset < shellcommand.length()
                ? shellcommand.substring(0, elementOffset)
                : shellcommand;
    }
}