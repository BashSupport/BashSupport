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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author jansorg
 */
public class HistoryExpansionParsingFunction implements ParsingFunction {
    @Override
    public boolean isValid(BashPsiBuilder builder) {
        IElementType token = builder.rawLookup(1);
        return token != null
                && ParserUtil.isWord(builder, "!")
                && !ParserUtil.isWhitespaceOrLineFeed(token);
    }

    private final TokenSet accepted = TokenSet.create(WORD, ARITH_NUMBER, DOLLAR);

    @Override
    public boolean parse(BashPsiBuilder builder) {
        //eat the ! token
        builder.advanceLexer();

        //following numbers specifiy the history entry
        //negative numbers count backwards
        //!! is an alias for !-1 which means the most recnt command in the history

        boolean ok = false;

        if (accepted.contains(builder.getTokenType())) {
            builder.advanceLexer();
            ok = true;
        } else if (Parsing.word.isComposedString(builder.getTokenType())) {
            ok = Parsing.word.parseComposedString(builder);
        } else {
            int count = 0;
            //fallback is here to eat up all token up to the first whitespace
            while (!builder.eof() && !ParserUtil.isWhitespaceOrLineFeed(builder.getTokenType(true))) {
                builder.advanceLexer();
                count++;
            }

            ok = count > 0;
        }

        return ok;
    }
}
