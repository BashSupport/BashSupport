/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbsolutePathCompletionProvider.java, Class: AbsolutePathCompletionProvider
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
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.util.ProcessingContext;

import java.util.Collections;
import java.util.List;

/**
 * This completion provider provides code completion for file / directory paths in the file.
 * <p/>
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 2:27:52 PM
 */
class AbsolutePathCompletionProvider extends BashCompletionProvider {
    //private static final Logger log = Logger.getInstance("AbsolutePathCompletionProvider");

    public AbsolutePathCompletionProvider() {
        super();
    }


    @Override
    protected List<String> addBashCompletions(String currentText, CompletionParameters parameters, ProcessingContext context, CompletionResultSet resultWithoutPrefix) {
        if (!currentText.startsWith("/")) {
            return Collections.emptyList();
        }

        return CompletionUtil.completeAbsolutePath(currentText);
    }
}
