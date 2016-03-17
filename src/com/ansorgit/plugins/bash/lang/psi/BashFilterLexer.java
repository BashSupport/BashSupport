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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.tree.IElementType;

class BashFilterLexer extends BaseFilterLexer {
    BashFilterLexer(Lexer lexer, OccurrenceConsumer consumer) {
        super(lexer, consumer);
    }

    @Override
    public void advance() {
        IElementType tokenType = myDelegate.getTokenType();

        if (tokenType == BashTokenTypes.COMMENT) {
            scanWordsInToken(UsageSearchContext.IN_COMMENTS, false, false);
            advanceTodoItemCountsInToken();
        } else if (tokenType == BashTokenTypes.STRING2) {
            addOccurrenceInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS | UsageSearchContext.IN_FOREIGN_LANGUAGES);
            scanWordsInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS, true, true);
        } else {
            addOccurrenceInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS);
            scanWordsInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS, true, false);
        }

        myDelegate.advance();
    }
}
