/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParser.java, Class: ArithmeticParser
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticFactory;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for arithmetic expressions.
 * It delegates to the actual arithmetic expression parser implementation but takes
 * care of start and end marker tokens.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:13:45
 *
 * @author Joachim Ansorg
 */
public final class ArithmeticParser implements ParsingFunction {
    private static final ParsingFunction arithmeticExprParser = ArithmeticFactory.entryPoint();

    public boolean isValid(BashPsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();
        return tokenType == BashTokenTypes.EXPR_ARITH || tokenType == BashTokenTypes.EXPR_ARITH_SQUARE;
    }

    /**
     * Parses a default arithmetic expression, e.g (( a+3 ))
     *
     * @param builder The builder to use
     * @return Whether the operation has been successful
     */
    public boolean parse(BashPsiBuilder builder) {
        if (builder.getTokenType() == BashTokenTypes.EXPR_ARITH_SQUARE) {
            return parse(builder, BashTokenTypes.EXPR_ARITH_SQUARE, BashTokenTypes._EXPR_ARITH_SQUARE);
        }

        return parse(builder, BashTokenTypes.EXPR_ARITH, BashTokenTypes._EXPR_ARITH);
    }

    /**
     * Parses an arithmetic expression with specific start and end token. This
     * is useful for places where a different syntax as (()) is used, e.g. is
     * array assignment lists.
     *
     * @param builder    The builder to use
     * @param startToken The expected start token
     * @param endToken   The expected end token
     * @return The result
     */
    public boolean parse(BashPsiBuilder builder, IElementType startToken, IElementType endToken) {
        /*
            arith_command:	ARITH_CMD
         */
        if (builder.getTokenType() != startToken) {
            return false;
        }

        final PsiBuilder.Marker arithmetic = builder.mark();
        builder.advanceLexer(); //after the start token

        if (!arithmeticExprParser.parse(builder)) {
            builder.getTokenType();
            arithmetic.drop();
            ParserUtil.error(builder, "parser.unexpected.token");
            return false;
        }

        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != endToken) {
            arithmetic.drop();
            ParserUtil.error(builder, "parser.unexpected.token");
            return false;
        }

        arithmetic.done(BashElementTypes.ARITHMETIC_COMMAND);
        return true;
    }
}
