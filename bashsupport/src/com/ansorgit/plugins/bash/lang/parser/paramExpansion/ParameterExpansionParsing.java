/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ParameterExpansionParsing.java, Class: ParameterExpansionParsing
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

package com.ansorgit.plugins.bash.lang.parser.paramExpansion;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.BashSmartMarker;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.misc.WordParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.google.common.base.Function;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Handles the default parsing of yet unknown / unsupported parameter expansions.
 * <p/>
 * User: jansorg
 * Date: 03.12.10
 * Time: 19:40
 *
 * fixme rewrite this parsing function, it doesn't support all cases yet is too complicated to maintain
 */
public class ParameterExpansionParsing implements ParsingFunction {
    private static final TokenSet validTokens = TokenSet.orSet(TokenSet.create(PARAM_EXPANSION_OP_UNKNOWN, LEFT_SQUARE, RIGHT_SQUARE, LEFT_PAREN, RIGHT_PAREN), paramExpansionOperators);
    private static final TokenSet prefixlessExpansionsOperators = TokenSet.create(PARAM_EXPANSION_OP_HASH);
    private static final TokenSet singleExpansionOperators = TokenSet.create(PARAM_EXPANSION_OP_AT, PARAM_EXPANSION_OP_QMARK);
    private static final TokenSet substitutionOperators = TokenSet.create(PARAM_EXPANSION_OP_COLON_MINUS, PARAM_EXPANSION_OP_COLON_QMARK, PARAM_EXPANSION_OP_COLON_PLUS);

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == LEFT_CURLY;
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        ParserUtil.getTokenAndAdvance(builder);

        //fixme check token type, only certain tokens are allowed

        if (builder.getTokenType(true) == PARAM_EXPANSION_OP_EXCL) {
            //indirect variable reference
            builder.advanceLexer(true);
        }

        IElementType firstToken = builder.getTokenType(true);
        if (singleExpansionOperators.contains(firstToken)) {
            builder.advanceLexer(true);
            firstToken = builder.getTokenType(true);

            if (firstToken == RIGHT_CURLY) {
                builder.advanceLexer(true);
                marker.done(PARAM_EXPANSION_ELEMENT);
                return true;
            }
        }

        //some tokens, like the length expansion '#' must not have a prefixes word character
        if (prefixlessExpansionsOperators.contains(firstToken)) {
            builder.advanceLexer(true);
            firstToken = builder.getTokenType(true);

            if (firstToken == WHITESPACE) {
                builder.error("Expected a variable.");
                marker.drop();
                return false;
            }
        }

        //the first token has to be a plain word token
        BashSmartMarker firstElementMarker = new BashSmartMarker(builder.mark());

        if (firstToken != DOLLAR && !ParserUtil.isWordToken(firstToken)) {
            builder.error("Expected a variable.");
            firstElementMarker.drop();
            marker.drop();

            return false;
        }

        //the first element is a word token, now check if it is a var use or var def token

        //eat the first token
        builder.advanceLexer(true);

        boolean markedAsVar = false;
        boolean isValid = true;
        boolean readFurther = true;

        if (builder.getTokenType(true) != RIGHT_CURLY) {
            IElementType operator = builder.getTokenType(true);

            //array reference
            if (operator == LEFT_SQUARE) {
                //one of x[*] or x[@]
                boolean isSpecialReference = ParserUtil.hasNextTokens(builder, false, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE)
                        || ParserUtil.hasNextTokens(builder, false, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE);

                boolean isValidReference = ParserUtil.checkAndRollback(builder, new Function<BashPsiBuilder, Boolean>() {
                    public Boolean apply(BashPsiBuilder builder) {
                        return Parsing.shellCommand.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                    }
                });

                if (isSpecialReference || isValidReference) {
                    firstElementMarker.done(VAR_ELEMENT);
                }

                //now parse the reference in the square brackets
                if (isSpecialReference) {
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                } else {
                    boolean validArrayReference = Parsing.shellCommand.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                    if (!validArrayReference) {
                        firstElementMarker.drop();
                        marker.drop();
                        return false;
                    }
                }
            } else if (substitutionOperators.contains(operator)) {
                //operators which perform substitution on the variable value, e.g. operator :-
                // the operator ":-" means that the variable in front is replaced with the value after the operator if it is null

                //eat the operator so it's not included in the replacement value
                firstElementMarker.done(VAR_ELEMENT);
                markedAsVar = true;

                builder.advanceLexer(true);

                //eat all tokens until we reach the closing } bracket
                readFurther = false;

                boolean wordIsOk = true;

                //fixme refactor this

                PsiBuilder.Marker replacementValueMarker = builder.mark();
                while (builder.getTokenType() != RIGHT_CURLY && wordIsOk && !builder.eof()) {
                    if (Parsing.word.isWordToken(builder)) {
                        //we have to accept variables, substitutions, etc. as well as substitution value
                        wordIsOk = Parsing.word.parseWord(builder, false, TokenSet.create(RIGHT_CURLY), TokenSet.EMPTY);
                    } else {
                        builder.advanceLexer();
                    }
                }

                if (builder.getTokenType() == RIGHT_CURLY) {
                    replacementValueMarker.collapse(WORD);
                } else {
                    replacementValueMarker.drop();
                }
            } else {
                if (!paramExpansionOperators.contains(operator)) {
                    firstElementMarker.drop();

                    marker.drop();
                    return false;
                }

                if (paramExpansionAssignmentOps.contains(operator)) {
                    firstElementMarker.done(VAR_DEF_ELEMENT);
                    builder.advanceLexer(true);
                    markedAsVar = true;
                } else if (paramExpansionOperators.contains(operator)) {
                    //unknown operator
                    firstElementMarker.done(VAR_ELEMENT);
                    builder.advanceLexer(true);
                    markedAsVar = true;
                } else {
                    //something else, e.g. indirect variable reference
                    firstElementMarker.drop();
                }
            }

            while (readFurther && isValid && builder.getTokenType() != RIGHT_CURLY) {
                if (Parsing.var.isValid(builder)) {
                    isValid = Parsing.var.parse(builder);
                } else if (Parsing.word.isComposedString(builder.getTokenType())) {
                    isValid = Parsing.word.parseComposedString(builder);
                } else if (Parsing.shellCommand.backtickParser.isValid(builder)) {
                    isValid = Parsing.shellCommand.backtickParser.parse(builder);
                } else {
                    isValid = readComposedValue(builder);
                }
            }
        } else {
            firstElementMarker.done(VAR_ELEMENT);
        }

        //make sure the first marker is closed before the closing marker
        if (firstElementMarker.isOpen()) {
            firstElementMarker.drop();
        }

        IElementType endToken = ParserUtil.getTokenAndAdvance(builder);
        boolean validEnd = RIGHT_CURLY == endToken;

        if (validEnd && !markedAsVar) {
            marker.done(PARAM_EXPANSION_ELEMENT);
        } else {
            marker.drop();
        }

        return validEnd && isValid;
    }

    private boolean readComposedValue(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        int count = 0;
        IElementType next = builder.getTokenType(true);
        while (validTokens.contains(next) || ParserUtil.isWordToken(next)) {
            builder.advanceLexer(true);
            count++;

            next = builder.getTokenType(true);
        }

        if (count > 0) {
            marker.collapse(WORD);
        } else {
            marker.drop();
        }

        return count > 0;
    }
}
