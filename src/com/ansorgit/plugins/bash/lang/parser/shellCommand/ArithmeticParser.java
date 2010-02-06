/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParser.java, Class: ArithmeticParser
 * Last modified: 2010-02-06
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticExprParser;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 02.05.2009
 * Time: 11:13:45
 *
 * @author Joachim Ansorg
 */
public final class ArithmeticParser extends DefaultParsingFunction {
    private ParsingFunction parsingFunction = new ArithmeticExprParser();

    public boolean isValid(IElementType token) {
        return token == BashTokenTypes.EXPR_ARITH;
    }

    /**
     * Parses a default arithmetic expression, e.g (( a+3 ))
     *
     * @param builder The builder to use
     * @return Whether the operation has been successful
     */
    public boolean parse(BashPsiBuilder builder) {
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
        builder.advanceLexer();//after the start token

        if (!parsingFunction.parse(builder)) {
            ParserUtil.error(arithmetic, "parser.unexpected.token");
            return false;
        }

        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != endToken) {
            ParserUtil.error(arithmetic, "parser.unexpected.token");
            return false;
        }

        arithmetic.done(BashElementTypes.ARITHMETIC_COMMAND);
        return true;
    }
}
