/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParameterExpansionParsing.java, Class: ParameterExpansionParsing
 * Last modified: 2010-01-29
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * User: jansorg
 * Date: Jan 27, 2010
 * Time: 8:48:33 PM
 */
public class ParameterExpansionParsing implements ParsingFunction {
    private final TokenSet substitutionEnd = TokenSet.create(RIGHT_CURLY);
    private final TokenSet validTokens = TokenSet.create(LEFT_SQUARE, RIGHT_SQUARE, PARAM_EXPANSION_OP);


    private static final Logger log = Logger.getInstance("#ParamExpansion");

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == LEFT_CURLY;
    }

    public boolean isValid(IElementType token) {
        throw new IllegalStateException("Can't check with single element");
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        final IElementType firstToken = ParserUtil.getTokenAndAdvance(builder);

        //the first token has to be a plain word token
        IElementType paramToken = builder.getTokenType();
        if (!ParserUtil.isWordToken(paramToken)) {
            marker.drop();
            return false; //fixme fail gracefully?
        }

        PsiBuilder.Marker varMarker = builder.mark();
        builder.advanceLexer();
        varMarker.done(BashElementTypes.VAR_ELEMENT);

        //Parsing.word.parseWord(builder, false, substitutionEnd, TokenSet.EMPTY);

        boolean markedAsVar = false;
        while (true) {
            if (builder.getTokenType() == RIGHT_CURLY) {
                break;
            }

            IElementType next = ParserUtil.getTokenAndAdvance(builder);
            if (!validTokens.contains(next)) {
                break;
            }
        }

        IElementType endToken = ParserUtil.getTokenAndAdvance(builder);
        boolean validEnd = RIGHT_CURLY == endToken;
        if (validEnd && !markedAsVar) {
            marker.done(VAR_SUBSTITUTION_ELEMENT);
        } else {
            marker.drop();
        }

        return validEnd;
    }
}
