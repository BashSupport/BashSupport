/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SimpleExpressionsImpl.java, Class: SimpleExpressionsImpl
 * Last modified: 2010-04-17
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

package com.ansorgit.plugins.bash.lang.psi.impl.arithmetic;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.SimpleExpression;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

import java.util.List;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:13:49 PM
 */
public class SimpleExpressionsImpl extends AbstractExpression implements SimpleExpression {
    private Boolean isStatic = null;

    public SimpleExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithSimpleExpr", Type.NoOperands);
    }

    @Override
    public boolean isStatic() {
        //fixme check if we need thread-safeness

        if (isStatic == null) {
            //it can have one operator in front followed by a simple expression
            //or just contain a number

            IElementType first = BashPsiUtils.nodeType(getFirstChild());

            if (BashTokenTypes.arithmeticAdditionOps.contains(first)) {
                List<ArithmeticExpression> subexpressions = subexpressions();
                isStatic = subexpressions.size() == 1 && subexpressions.get(0).isStatic();
            } else {
                isStatic = (first == BashTokenTypes.NUMBER);
            }
        }

        return isStatic;
    }

    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public long computeNumericValue() {
        if (isStatic()) {
            String asString = getText();
            try {
                return Long.valueOf(asString);
            } catch (NumberFormatException e) {
                //fixme
                return 0;
            }
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
    }
}
