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
import com.ansorgit.plugins.bash.lang.parser.Parsing;
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
    private enum ExressionType {
        Sum, Product, Misc, Simple
    }

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

        /*while (builder.getTokenType() != endToken && isArithmeticToken(builder.getTokenType())) {
            readArithmeticPart(builder);
        } */

        readArithmeticExpression(builder, true);

        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != endToken) {
            ParserUtil.error(arithmetic, "parser.unexpected.token");
            return false;
        }


        arithmetic.done(BashElementTypes.ARITHMETIC_COMMAND);
        return true;
    }

    /**
     * Grammar:
     * <p/>
     * MUL = SUM | MUL * MUL | MUL * MUL
     * SUM = SIMPLE| SUM + SUM | SUM - SUM
     * SIMPLE = var | NUM
     *
     * @param builder          The PSI builder
     * @param acceptAssignment True if assignments are valid in the expected expression
     * @return True if the expressions was successfully parser
     */
    public boolean readArithmeticExpression(BashPsiBuilder builder, boolean acceptAssignment) {
        if (acceptAssignment && isAssignment(builder)) {
            PsiBuilder.Marker start = builder.mark();
            PsiBuilder.Marker marker = start;

            start = marker.precede();

            if (Parsing.var.isValid(builder)) {
                Parsing.var.parse(builder);
            } else {
                //fixme check for token type
                builder.advanceLexer(); //read simple assignment word tokens
            }

            marker.done(VAR_DEF_ELEMENT);

            //eat operator
            builder.getTokenType();//check so parser does not complain
            builder.advanceLexer();

            boolean ok = readArithmeticExpression(builder, false);

            if (ok) {
                start.done(ARITH_ASSIGNMENT);
            } else {
                start.drop();
            }

            return ok;
        } else if (isParenthesesExpr(builder)) {
            parseParenthesesExpr(builder);
        }

        return parseSumExpr(builder, true);
    }

    private boolean isParenthesesExpr(BashPsiBuilder builder) {
        return builder.getTokenType() == LEFT_PAREN;
    }

    private boolean parseParenthesesExpr(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        boolean hasParens = builder.getTokenType() == LEFT_PAREN;

        if (hasParens) {
            builder.advanceLexer();
        }

        boolean ok = readArithmeticExpression(builder, false);

        if (ok && hasParens && builder.getTokenType() == RIGHT_PAREN) {
            builder.advanceLexer();
            marker.done(ARITH_PARENS);
        } else {
            marker.drop();
        }

        return ok;
    }

    private boolean parseSumExpr(BashPsiBuilder builder, boolean mark) {
        PsiBuilder.Marker marker = mark ? builder.mark() : null;

        boolean ok = parseProductExpr(builder, true);

        boolean hasSum = ok && arithmeticAdditionOps.contains(builder.getTokenType());
        if (hasSum) {
            builder.advanceLexer();
            ok = parseOptionalParenthesesExpr(builder, ExressionType.Sum, false);
        }

        if (ok && mark && hasSum) {
            marker.done(ARITH_SUM);
        } else if (mark) {
            marker.drop();
        }

        return ok;
    }

    private boolean parseOptionalParenthesesExpr(BashPsiBuilder builder, ExressionType expected, boolean mark) {
        if (isParenthesesExpr(builder)) {
            return parseParenthesesExpr(builder);
        } else {
            switch (expected) {
                case Sum:
                    return parseSumExpr(builder, mark);
                case Product:
                    return parseProductExpr(builder, mark);
                case Simple:
                    return parseSimpleExpr(builder);
                default:
                    throw new IllegalStateException("Invalid case value");
            }
        }
    }

    private boolean parseProductExpr(BashPsiBuilder builder, boolean mark) {
        PsiBuilder.Marker marker = mark ? builder.mark() : null;

        boolean ok = parseSimpleExpr(builder);

        boolean hasProduct = ok && arithmeticProduct.contains(builder.getTokenType());
        if (hasProduct) {
            builder.advanceLexer();
            ok = parseOptionalParenthesesExpr(builder, ExressionType.Product, false);
        }

        if (ok && mark && hasProduct) {
            marker.done(ARITH_MUL);
        } else if (mark) {
            marker.drop();
        }

        return ok;
    }

    private boolean parseMiscExpr(BashPsiBuilder builder) {
        //fixme
        return false;
    }

    private boolean parseSimpleExpr(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        boolean ok = false;
        if (Parsing.var.isValid(builder)) {
            //$ prefixed variables, e.g. $a or $(echo 1)
            ok = Parsing.var.parse(builder);
        } else if (builder.getTokenType() == WORD) {
            //a simple word is a variable in arithmetic expressions
            ParserUtil.markTokenAndAdvance(builder, VAR_ELEMENT);
            ok = true;
        } else if (isSimpleExprToken(builder.getTokenType())) {
            builder.advanceLexer();
            ok = true;
        }

        if (ok) {
            marker.done(ARITH_SIMPLE);
        } else {
            marker.drop();
        }

        return ok;
    }

    private boolean isAssignment(BashPsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();
        try {
            if (Parsing.var.isValid(builder)) {
                Parsing.var.parse(builder);
            } else if (builder.getTokenType() == WORD || builder.getTokenType() == ASSIGNMENT_WORD) {
                builder.advanceLexer();
            }

            return arithmeticAssign.contains(builder.getTokenType());
        }
        finally {
            start.rollbackTo();
        }
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

    private boolean isSimpleExprToken(IElementType tokenType) {
        return tokenType == BashTokenTypes.NUMBER
                || tokenType == BashTokenTypes.WORD
                || tokenType == BashTokenTypes.ASSIGNMENT_WORD
                || tokenType == BashTokenTypes.EQ;
    }
}
