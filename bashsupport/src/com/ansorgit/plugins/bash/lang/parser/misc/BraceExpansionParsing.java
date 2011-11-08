/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BraceExpansionParsing.java, Class: BraceExpansionParsing
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing function for brace expansions.
 * <p/>
 * User: jansorg
 * Date: Nov 14, 2009
 * Time: 11:57:59 PM
 */
public class BraceExpansionParsing implements ParsingFunction {
    private static final TokenSet validExpansionTokens = TokenSet.create(WORD, INTEGER_LITERAL);

    public boolean isValid(BashPsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        try {
            return parse(builder);
        } finally {
            start.rollbackTo();
        }
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        //read in the prefix
        while (validExpansionTokens.contains(builder.getTokenType(true))) {
            builder.advanceLexer(true);
        }

        //don't accept a prefix without the actual expansion block
        if (builder.getTokenType(true) != LEFT_CURLY) {
            marker.drop();
            return false;
        }

        while (builder.getTokenType(true) == LEFT_CURLY) {
            builder.advanceLexer(true); //eat the left curly

            boolean isExpansion = builder.getTokenType(true) != WHITESPACE;
            if (!isExpansion) {
                marker.drop();
                return false;
            }

            //fixme check if the last char before the right curly bracket is a whitespace or not
            //make sure we don't run into endless parsing or don't stop if there's a missing right curly bracket
            while (validExpansionTokens.contains(builder.getTokenType(true))) {
                builder.advanceLexer(true);
                if (builder.getTokenType(true) == COMMA) {
                    builder.advanceLexer(true);
                }
            }

            if (builder.getTokenType(true) != RIGHT_CURLY) {
                marker.drop();
                return false;
            }

            //eat the right curly bracket
            builder.advanceLexer(true);

            //now read in all static suffix text
            while (builder.getTokenType(true) == WORD) {
                builder.advanceLexer(true);
            }
        }

        marker.done(EXPANSION_ELEMENT);
        return true;
    }
}
