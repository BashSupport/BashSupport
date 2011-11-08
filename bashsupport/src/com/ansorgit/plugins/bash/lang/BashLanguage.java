/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLanguage.java, Class: BashLanguage
 * Last modified: 2010-03-09 21:46
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

package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.editor.highlighting.BashSyntaxHighlighter;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.SingleLazyInstanceSyntaxHighlighterFactory;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 22.03.2009
 * Time: 11:12:46
 *
 * @author Joachim Ansorg
 */
public class BashLanguage extends Language {
    public BashLanguage() {
        super("Bash", "application/x-bsh", "application/x-sh", "text/x-script.sh");

        SyntaxHighlighterFactory.LANGUAGE_FACTORY.addExplicitExtension(this, new BashHighlighterFactory());
    }

    private static class BashHighlighterFactory extends SingleLazyInstanceSyntaxHighlighterFactory {
        @NotNull
        protected SyntaxHighlighter createHighlighter() {
            return new BashSyntaxHighlighter();
        }
    }
}
