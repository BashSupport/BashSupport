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

package com.ansorgit.plugins.bash.lang.psi.impl.arithmetic;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.SimpleExpression;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author jansorg
 */
public class SimpleExpressionsImpl extends AbstractExpression implements SimpleExpression {
    private static final char[] literalChars;

    static {
        literalChars = new char[64];
        int index = 0;

        //index 0-9
        for (char c = '0'; c <= '9'; c++) {
            literalChars[index++] = c;
        }

        //index 10-35
        for (char c = 'a'; c <= 'z'; c++) {
            literalChars[index++] = c;
        }

        //index 36-61
        for (char c = 'A'; c <= 'Z'; c++) {
            literalChars[index++] = c;
        }

        //index 62
        literalChars[index++] = '@';

        //index 63
        literalChars[index] = '_';
    }

    private final Object stateLock = new Object();
    private volatile LiteralType literalType;
    private volatile Boolean isStatic = null;

    public SimpleExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithSimpleExpr", Type.NoOperands);
    }

    public LiteralType literalType() {
        if (literalType == null) {
            //no other lock is used in the callees, it's safe to synchronize around the whole calculation
            synchronized (stateLock) {
                if (literalType == null) {
                    LiteralType newType = LiteralType.Other;

                    PsiElement child = getFirstChild();
                    if (child != null && BashTokenTypes.arithmeticAdditionOps.contains(PsiUtilCore.getElementType(child))) {
                        //ignore prefix operators
                        child = child.getNextSibling();
                    }

                    if (child != null) {
                        IElementType elementType = PsiUtilCore.getElementType(child);

                        PsiElement second = child.getNextSibling();
                        IElementType typeSecond = second != null ? PsiUtilCore.getElementType(second) : null;

                        if (elementType == BashTokenTypes.ARITH_HEX_NUMBER) {
                            newType = LiteralType.HexLiteral;
                        } else if (elementType == BashTokenTypes.ARITH_OCTAL_NUMBER) {
                            newType = LiteralType.OctalLiteral;
                        } else if (elementType == BashTokenTypes.ARITH_NUMBER) {
                            if (typeSecond == BashTokenTypes.ARITH_BASE_CHAR) {
                                newType = LiteralType.BaseLiteral;
                            } else {
                                newType = LiteralType.DecimalLiteral;
                            }
                        }
                    }

                    literalType = newType;
                }
            }
        }

        return literalType;
    }

    @Override
    public boolean isStatic() {
        if (isStatic == null) {
            //no other lock is used in the callees, it's safe to synchronize around the whole calculation
            synchronized (stateLock) {
                if (isStatic == null) {
                    //it can have one operator in front followed by a simple expression
                    //or just contain a number

                    ASTNode[] children = getNode().getChildren(null);
                    boolean newIsStatic = false;

                    if (children.length > 0) {
                        IElementType first = BashPsiUtils.getDeepestEquivalent(children[0]).getElementType();

                        if (LiteralType.BaseLiteral.equals(literalType())) {
                            newIsStatic = children.length == 3;
                            if (newIsStatic) {
                                IElementType secondType = BashPsiUtils.getDeepestEquivalent(children[2]).getElementType();

                                newIsStatic = secondType == BashTokenTypes.WORD
                                        || secondType == BashElementTypes.PARSED_WORD_ELEMENT
                                        || BashTokenTypes.arithLiterals.contains(secondType);
                            }
                        } else if (children.length == 2 && BashTokenTypes.arithmeticAdditionOps.contains(first)) {
                            List<ArithmeticExpression> subexpressions = subexpressions();
                            newIsStatic = (subexpressions.size() == 1) && subexpressions.get(0).isStatic();
                        } else if (children.length == 1) {
                            newIsStatic = BashTokenTypes.arithLiterals.contains(first);
                        }
                    }

                    isStatic = newIsStatic;
                }
            }
        }

        return isStatic;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        synchronized (stateLock) {
            isStatic = null;
            literalType = null;
        }
    }

    @Nullable
    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new IllegalStateException("SimpleExpressionImpl: Unsupported for " + getText());
    }

    @Override
    public long computeNumericValue() throws InvalidExpressionValue {
        if (isStatic()) {
            ASTNode[] children = getNode().getChildren(null);

            // a base literal has several child elements
            if (literalType() == LiteralType.BaseLiteral) {
                if (children.length != 3) {
                    throw new IllegalStateException("unexpected number of children for a numeric valid with base");
                }

                String baseText = children[0].getText();
                String numericText = children[2].getText();
                try {
                    return baseLiteralValue(Long.valueOf(baseText), numericText);
                } catch (NumberFormatException e) {
                    //shouldn't happen because the lexer only lexed ints here
                    throw new InvalidExpressionValue("Invalid numeric base value: " + baseText);
                }
            } else if (children.length == 1) {
                String asString = getText();

                try {
                    LiteralType currentLiteralType = literalType();

                    switch (currentLiteralType) {
                        case DecimalLiteral:
                            return Long.valueOf(asString);

                        case HexLiteral:
                            //we cut of the 0x prefix
                            return Long.valueOf(asString.substring(2), 16);

                        case OctalLiteral:
                            return Long.valueOf(asString, 8);

                        default:
                            throw new IllegalStateException("Illegal state, neither decimal, hex nor base literal: " + currentLiteralType + ", " + asString + ", " + DebugUtil.psiToString(getParent(), false, true));
                    }
                } catch (NumberFormatException e) {
                    //fixme
                    return 0;
                }
            } else {
                if (children.length == 0) {
                    throw new IllegalStateException("Unexpected number of child elements: " + getText());
                }

                //probably a prefixed expression with + -
                ASTNode first = children[0];
                if (children.length > 1 && !(children[1].getPsi() instanceof ArithmeticExpression)) {
                    throw new IllegalStateException("invalid expression found");
                }

                AbstractExpression second = (AbstractExpression) children[1].getPsi();

                IElementType nodeType = first.getElementType();
                if (nodeType == BashTokenTypes.ARITH_MINUS) {
                    return -1 * second.computeNumericValue();
                }

                if (nodeType == BashTokenTypes.ARITH_PLUS) {
                    return second.computeNumericValue();
                }

                throw new IllegalStateException("Invalid state found (invalid prefix operator); " + getText());
            }
        }

        throw new InvalidExpressionValue("unsupported expression state: " + getText());
    }

    static long baseLiteralValue(long base, String value) throws InvalidExpressionValue {
        long result = 0;

        int index = value.length() - 1;
        for (char c : value.toCharArray()) {
            long digitValue = baseLiteralValue(base, c);
            if (digitValue == -1) {
                throw new InvalidExpressionValue("Digit " + c + " is invalid with base " + base);
            }

            result += Math.pow(base, index) * digitValue;
            index--;
        }

        return result;
    }

    static long baseLiteralValue(long base, char value) {
        // Constants with a leading 0 are interpreted as octal numbers.
        // A leading ‘0x’ or ‘0X’ denotes hexadecimal. Otherwise, numbers take the form [base#]n,
        // where base is a decimal number between 2 and 64 representing the arithmetic base,
        // and n is a number in that base. If base#  is omitted, then base 10 is used.
        // The digits greater than 9 are represented by the lowercase letters,
        // the uppercase letters, ‘@’, and ‘_’, in that order.
        // If base is less than or equal to 36, lowercase and uppercase letters may be used
        // interchangeably to represent numbers between 10 and 35.

        long result = -1;

        for (int i = 0; i < literalChars.length; i++) {
            char c = literalChars[i];

            if (c == value) {
                if (base <= 36 && i >= 36 && i <= 61) {
                    result = i - 36 + 10;
                } else if (i <= base) {
                    result = i;
                }

                break;
            }
        }

        return result < base ? result : -1;
    }
}
