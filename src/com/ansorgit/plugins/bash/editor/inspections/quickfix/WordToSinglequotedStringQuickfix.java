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

package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import org.jetbrains.annotations.NotNull;

/**
 * Converts a word like into an unquoted string 'a'.
 * <br>
 * @author jansorg
 */
public class WordToSinglequotedStringQuickfix extends AbstractWordWrapQuickfix {

    public WordToSinglequotedStringQuickfix(BashWord word) {
        super(word);
    }

    protected String wrapText(String text) {
        return "'" + text + "'";
    }

    @NotNull
    public String getText() {
        return "Convert to unquoted string '...'";
    }

}