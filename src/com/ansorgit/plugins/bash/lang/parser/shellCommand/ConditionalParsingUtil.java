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

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author jansorg
 */
public final class ConditionalParsingUtil {
    private static final TokenSet operators = TokenSet.create(BashTokenTypes.COND_OP, BashTokenTypes.COND_OP_EQ_EQ, BashTokenTypes.COND_OP_REGEX);
    private static final TokenSet regExpEndTokens = TokenSet.create(BashTokenTypes.WHITESPACE, BashTokenTypes._BRACKET_KEYWORD);

    private ConditionalParsingUtil() {
    }

    public static boolean readTestExpression(BashPsiBuilder builder, TokenSet endTokens) {
        //fixme implement more intelligent test expression parsing

        boolean ok = true;

        while (ok && !endTokens.contains(builder.getTokenType())) {
            OptionalParseResult result = Parsing.word.parseWordIfValid(builder);
            if (result.isValid()) {
                ok = result.isParsedSuccessfully();
            } else if (builder.getTokenType() == BashTokenTypes.COND_OP_NOT) {
                builder.advanceLexer();
                ok = readTestExpression(builder, endTokens);
            } else if (builder.getTokenType() == BashTokenTypes.COND_OP_REGEX) {
                builder.advanceLexer();

                //parse the regex
                ok = parseRegularExpression(builder, endTokens);
            } else if (operators.contains(builder.getTokenType())) {
                builder.advanceLexer();
            } else {
                ok = false;
                break;
            }
        }

        return ok;
    }

    public static boolean parseRegularExpression(BashPsiBuilder builder, TokenSet endMarkerTokens) {
        int count = 0;

        //simple solution: read to the next whitespace, unless we are in [] brackets
        while (!builder.eof()) {
            IElementType current = builder.getTokenType(true);
            if (count == 0 && current == BashTokenTypes.WHITESPACE) {
                builder.advanceLexer();
                continue;
            }

            if (endMarkerTokens.contains(current)) {
                break;
            }

            if (Parsing.word.isComposedString(current)) {
                if (!Parsing.word.parseComposedString(builder)) {
                    break;
                }
            } else if (!regExpEndTokens.contains(current)) {
                builder.advanceLexer();
            } else {
                break;
            }

            count++;
        }

        return count > 0;
    }
}
