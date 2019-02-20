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

package com.ansorgit.plugins.bash.lang.parser.paramExpansion;

import com.ansorgit.plugins.bash.lang.parser.*;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Handles the default parsing of yet unknown / unsupported parameter expansions.
 * <br>
 *
 * @author jansorg
 * <br>
 * fixme rewrite this parsing function, it doesn't support all cases yet is too complicated to maintain
 */
public class ParameterExpansionParsing implements ParsingFunction {
    private static final TokenSet validTokens = TokenSet.orSet(paramExpansionOperators, TokenSet.create(
            PARAM_EXPANSION_OP_UNKNOWN, LEFT_SQUARE, RIGHT_SQUARE, LEFT_PAREN, RIGHT_PAREN, LINE_FEED,
            LESS_THAN, GREATER_THAN)
    );

    private static final TokenSet prefixlessExpansionsOperators = TokenSet.create(PARAM_EXPANSION_OP_HASH);

    private static final TokenSet singleExpansionOperators = TokenSet.create(PARAM_EXPANSION_OP_AT,
            PARAM_EXPANSION_OP_QMARK, DOLLAR, PARAM_EXPANSION_OP_EXCL, PARAM_EXPANSION_OP_MINUS,
            PARAM_EXPANSION_OP_STAR, ARITH_NUMBER, PARAM_EXPANSION_OP_HASH, PARAM_EXPANSION_OP_HASH_HASH);

    private static final TokenSet variableMarkingExpansionOperators = TokenSet.create(PARAM_EXPANSION_OP_AT,
            PARAM_EXPANSION_OP_STAR);

    private static final TokenSet substitutionOperators = TokenSet.create(PARAM_EXPANSION_OP_COLON_MINUS,
            PARAM_EXPANSION_OP_COLON_QMARK, PARAM_EXPANSION_OP_COLON_PLUS);

    private static final TokenSet validFirstTokens = TokenSet.create(DOLLAR, PARAM_EXPANSION_OP_AT, PARAM_EXPANSION_OP_STAR);
    private static final TokenSet TOKEN_SET_CURLY_RIGHT = TokenSet.create(RIGHT_CURLY);

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == LEFT_CURLY;
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        ParserUtil.getTokenAndAdvance(builder); //opening { bracket

        //special handling for empty expansions
        if (builder.rawLookup(0) == RIGHT_CURLY) {
            builder.advanceLexer();
            ParserUtil.error(marker, "parser.paramExpansion.empty");
            return true;
        }

        if (singleExpansionOperators.contains(builder.rawLookup(0)) && builder.rawLookup(1) == RIGHT_CURLY) {
            //fixme handle variable marking, i.e. $- etc.
            if (variableMarkingExpansionOperators.contains(builder.rawLookup(0))) {
                ParserUtil.markTokenAndAdvance(builder, VAR_ELEMENT);
            } else {
                ParserUtil.getTokenAndAdvance(builder); //the single value token
            }
            ParserUtil.getTokenAndAdvance(builder); //the closing }
            marker.done(PARAM_EXPANSION_ELEMENT);
            return true;
        }

        if (builder.getTokenType(true) == PARAM_EXPANSION_OP_EXCL) {
            //indirect variable reference
            builder.advanceLexer();
        }

        //fixme check token type, only certain tokens are allowed
        IElementType firstToken = builder.getTokenType(true);

        //some tokens, like the length expansion '#' must not have a prefixes word character
        if (prefixlessExpansionsOperators.contains(firstToken)) {
            builder.advanceLexer();
            firstToken = builder.getTokenType(true);

            if (firstToken == WHITESPACE) {
                builder.error("Expected a variable.");
                marker.drop();
                return false;
            }
        }

        //the first token has to be a plain word token
        BashSmartMarker firstElementMarker = new BashSmartMarker(builder.mark());

        if (!validFirstTokens.contains(firstToken) && !ParserUtil.isWordToken(firstToken)) {
            if (!builder.isEvalMode() || Parsing.var.isInvalid(builder)) {
                builder.error("Expected a valid parameter expansion token.");
                firstElementMarker.drop();

                //try to minimize the error impact
                return readRemainingExpansionTokens(builder, marker);
            }
        }

        //the first element is a word token, now check if it is a var use or var def token

        //eat the first token
        if (builder.isEvalMode()) {
            OptionalParseResult varResult = Parsing.var.parseIfValid(builder);
            if (varResult.isValid()) {
                if (!varResult.isParsedSuccessfully()) {
                    firstElementMarker.drop();
                    return false;
                }
            } else {
                builder.advanceLexer();
            }
        } else {
            builder.advanceLexer();
        }

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

                //fixme optimize this
                boolean isValidReference = ParserUtil.checkAndRollback(builder, psiBuilder -> ShellCommandParsing.arithmeticParser.parse(psiBuilder, LEFT_SQUARE, RIGHT_SQUARE));

                if (isSpecialReference || isValidReference) {
                    firstElementMarker.done(VAR_ELEMENT);
                }

                //now parse the reference in the square brackets
                if (isSpecialReference) {
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                } else {
                    boolean validArrayReference = ShellCommandParsing.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
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

                builder.advanceLexer();

                //eat all tokens until we reach the closing } bracket
                readFurther = false;

                boolean wordIsOk = true;

                //fixme refactor this

                PsiBuilder.Marker replacementValueMarker = builder.mark();
                while (builder.getTokenType() != RIGHT_CURLY && wordIsOk && !builder.eof()) {
                    OptionalParseResult result = Parsing.word.parseWordIfValid(builder, false, TOKEN_SET_CURLY_RIGHT, TokenSet.EMPTY, null);
                    if (result.isValid()) {
                        //we have to accept variables, substitutions, etc. as well as substitution value
                        wordIsOk = result.isParsedSuccessfully();
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
                    builder.error("Unknown parameter expansion operator " + operator);
                    firstElementMarker.drop();

                    //try to minimize the error impact
                    return readRemainingExpansionTokens(builder, marker);
                }

                if (paramExpansionAssignmentOps.contains(operator)) {
                    firstElementMarker.done(VAR_DEF_ELEMENT);
                    builder.advanceLexer();
                    markedAsVar = true;
                } else if (variableMarkingExpansionOperators.contains(operator)) {
                    builder.advanceLexer();
                } else if (paramExpansionOperators.contains(operator)) {
                    //unknown operator
                    firstElementMarker.done(VAR_ELEMENT);
                    builder.advanceLexer();
                    markedAsVar = true;
                } else {
                    //something else, e.g. indirect variable reference
                    firstElementMarker.drop();
                }
            }

            while (readFurther && isValid && builder.getTokenType() != RIGHT_CURLY) {
                OptionalParseResult varResult = Parsing.var.parseIfValid(builder);
                if (varResult.isValid()) {
                    isValid = varResult.isParsedSuccessfully();
                } else if (Parsing.word.isComposedString(builder.getTokenType())) {
                    isValid = Parsing.word.parseComposedString(builder);
                } else if (Parsing.shellCommand.backtickParser.isValid(builder)) {
                    isValid = Parsing.shellCommand.backtickParser.parse(builder);
                } else {
                    isValid = readComposedValue(builder);
                }
            }
        } else if (builder.isEvalMode() && firstToken == VARIABLE) {
            firstElementMarker.drop();
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
            builder.advanceLexer();
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

    private boolean readRemainingExpansionTokens(BashPsiBuilder builder, PsiBuilder.Marker marker) {
        PsiBuilder.Marker start = builder.mark();

        int max = 10;
        while (!builder.eof() && builder.getTokenType() != RIGHT_CURLY && max > 0) {
            builder.advanceLexer();
            max--;
        }

        if (max <= 0) {
            start.rollbackTo();
            marker.drop();
            return false;
        }

        builder.advanceLexer();//eat the last } token
        start.drop();
        marker.done(PARAM_EXPANSION_ELEMENT);
        return true;
    }
}
