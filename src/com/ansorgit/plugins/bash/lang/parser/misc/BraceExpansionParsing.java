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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.ansorgit.plugins.bash.util.NullMarker;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing function for brace expansions.
 *
 * @author jansorg
 */
public class BraceExpansionParsing implements ParsingFunction {
    private static final TokenSet validExpansionTokens = TokenSet.create(WORD, INTEGER_LITERAL);

    /**
     * @param builder The provider of the psi tokens
     * @return
     */
    public boolean isValid(BashPsiBuilder builder) {
        //check if there is a curly bracket in the next tokens, if not then its not a brace expansion
        if (!ParserUtil.containsTokenInLookahead(builder, LEFT_CURLY, 10, false)) {
            return false;
        }

        PsiBuilder.Marker start = builder.mark();

        boolean result = doParse(builder, true);

        start.rollbackTo();

        return result;
    }

    public boolean parse(BashPsiBuilder builder) {
        return doParse(builder, false);
    }

    private boolean doParse(BashPsiBuilder builder, boolean checkMode) {
        PsiBuilder.Marker marker = checkMode ? NullMarker.get() : builder.mark();

        //read in the prefix
        while (true) {
            IElementType tokenType = builder.getTokenType(true);
            if (Parsing.word.isComposedString(tokenType)) {
                Parsing.word.parseComposedString(builder);
            } else {
                if (validExpansionTokens.contains(tokenType)) {
                    builder.advanceLexer();
                } else {
                    break;
                }
            }
        }

        //don't accept a prefix without the actual expansion block
        if (builder.getTokenType(true) != LEFT_CURLY) {
            marker.drop();
            return false;
        }

        while (builder.getTokenType(true) == LEFT_CURLY) {
            builder.advanceLexer(); //eat the left curly

            boolean isExpansion = builder.getTokenType(true) != WHITESPACE;
            if (!isExpansion) {
                marker.drop();
                return false;
            }

            //fixme check if the last char before the right curly bracket is a whitespace or not
            //make sure we don't run into endless parsing or don't stop if there's a missing right curly bracket
            while (validExpansionTokens.contains(builder.getTokenType(true))) {
                builder.advanceLexer();
                if (builder.getTokenType(true) == COMMA) {
                    builder.advanceLexer();
                }
            }

            if (builder.getTokenType(true) != RIGHT_CURLY) {
                marker.drop();
                return false;
            }

            //eat the right curly bracket
            builder.advanceLexer();

            //now read in all static suffix text
            while (builder.getTokenType(true) == WORD) {
                builder.advanceLexer();
            }
        }

        marker.done(EXPANSION_ELEMENT);
        return true;
    }
}
