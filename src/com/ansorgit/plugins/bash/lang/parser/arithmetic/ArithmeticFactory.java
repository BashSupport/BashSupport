/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticFactory.java, Class: ArithmeticFactory
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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * User: jansorg
 * Date: 17.07.2010
 * Time: 18:36:58
 */
public class ArithmeticFactory implements BashTokenTypes, BashElementTypes {
    private static ParenExpr parenExpr = new ParenExpr();

    private ArithmeticFactory() {
    }

    private static ArithmeticParsingFunction simpleExpression = new SimpleArithmeticExpr();
    private static ArithmeticParsingFunction postIncrement = new PostIncrementExpr(simpleExpression);
    private static ArithmeticParsingFunction preIncrement = new PreIncrementExpr(postIncrement);
    private static ArithmeticParsingFunction negation = repeated(preIncrement, arithmeticNegationOps, ARITH_NEGATION_ELEMENT, "negation");
    private static ArithmeticParsingFunction exponent = repeated(negation, ARITH_EXPONENT, ARITH_EXPONENT_ELEMENT, "exponent");
    private static ArithmeticParsingFunction multiplication = repeated(exponent, arithmeticProduct, ARITH_MULTIPLICACTION_ELEMENT, "mulitplication");
    private static ArithmeticParsingFunction addition = repeated(multiplication, arithmeticAdditionOps, ARITH_SUM_ELEMENT, "addition");
    private static ArithmeticParsingFunction shift = repeated(addition, arithmeticShiftOps, ARITH_SHIFT_ELEMENT, "shift");
    private static ArithmeticParsingFunction compoundComparision = repeated(shift, arithmeticCmpOp, ARITH_COMPUND_COMPARISION_ELEMENT, "compoundComparision");
    private static ArithmeticParsingFunction equality = repeated(compoundComparision, arithmeticEqualityOps, ARITH_EQUALITY_ELEMENT, "equality");
    private static ArithmeticParsingFunction bitwiseAnd = repeated(equality, ARITH_BITWISE_AND, ARITH_BIT_AND_ELEMENT, "bitwiseAnd");
    private static ArithmeticParsingFunction bitwiseXor = repeated(bitwiseAnd, ARITH_BITWISE_XOR, ARITH_BIT_XOR_ELEMENT, "bitwiseXor");
    private static ArithmeticParsingFunction bitwiseOr = repeated(bitwiseXor, PIPE, ARITH_BIT_OR_ELEMENT, "bitwiseOr");
    private static ArithmeticParsingFunction logicalAnd = repeated(bitwiseOr, AND_AND, ARITH_LOGIC_AND_ELEMENT, "bitwiseAnd");
    private static ArithmeticParsingFunction logicalOr = repeated(logicalAnd, OR_OR, ARITH_LOGIC_OR_ELEMENT, "logicalOr");
    private static TernaryExpression ternary = new TernaryExpression(logicalOr);
    private static ArithmeticParsingFunction simpleAssignment = new AbstractAssignment(ternary, TokenSet.create(EQ));
    private static ArithmeticParsingFunction assignmentCombination = new AbstractAssignment(simpleAssignment, arithmeticAssign);
    private static ArithmeticParsingFunction assignmentChain = repeated(assignmentCombination, COMMA, ARITH_ASSIGNMENT_CHAIN_ELEMENT, "assignmentChain");

    public static ArithmeticParsingFunction entryPoint() {
        return assignmentChain;
    }

    public static ArithmeticParsingFunction parenthesisParser() {
        return parenExpr;
    }

    private static ArithmeticParsingFunction repeated(ArithmeticParsingFunction next, TokenSet operators, IElementType marker, String debugInfo) {
        return new AbstractRepeatedExpr(next, operators, marker, -1, debugInfo);
    }

    private static ArithmeticParsingFunction repeated(ArithmeticParsingFunction next, IElementType operator, IElementType marker, String debugInfo) {
        return repeated(next, TokenSet.create(operator), marker, debugInfo);
    }
}
