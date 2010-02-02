/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParser.java, Class: ArithmeticParser
 * Last modified: 2009-12-04
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
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
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

        if (!isArithmeticToken(builder.getTokenType())) {
            ParserUtil.error(arithmetic, "parser.unexpected.token");
            return false;
        }

        while (builder.getTokenType() != endToken && isArithmeticToken(builder.getTokenType())) {
            readArithmeticPart(builder);
        }

        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != endToken) {
            ParserUtil.error(arithmetic, "parser.unexpected.token");
            return false;
        }


        arithmetic.done(BashElementTypes.ARITHMETIC_COMMAND);
        return true;
    }

    public boolean readArithmeticPart(BashPsiBuilder builder) {
        final IElementType tokenType = builder.getTokenType();
        if (!isArithmeticToken(tokenType)) {
            return false;
        }

        if (Parsing.var.isValid(builder)) {
            return Parsing.var.parse(builder);
        } else if (CommandParsingUtil.isAssignment(builder, CommandParsingUtil.Mode.StrictAssignmentMode)) {
            return CommandParsingUtil.readAssignment(builder, CommandParsingUtil.Mode.StrictAssignmentMode, true);
        }

        //now we assume a simple token
        builder.advanceLexer();
        return true;
    }

    private boolean isArithmeticToken(IElementType tokenType) {
        return tokenType == BashTokenTypes.NUMBER
                || tokenType == BashTokenTypes.WORD
                || tokenType == BashTokenTypes.ASSIGNMENT_WORD
                || tokenType == BashTokenTypes.EQ
                || Parsing.var.isValid(tokenType)
                || BashTokenTypes.arithmeticAssign.contains(tokenType)
                || BashTokenTypes.arithmeticCmp.contains(tokenType)
                || BashTokenTypes.arithmeticLogic.contains(tokenType)
                || BashTokenTypes.arithmeticMinus.contains(tokenType)
                || BashTokenTypes.arithmeticMisc.contains(tokenType)
                || BashTokenTypes.arithmeticPlus.contains(tokenType);
    }
}
