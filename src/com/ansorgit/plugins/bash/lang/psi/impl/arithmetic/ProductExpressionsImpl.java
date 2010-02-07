/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ProductExpressionsImpl.java, Class: ProductExpressionsImpl
 * Last modified: 2010-02-07
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
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ProductExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

import java.util.List;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:13:49 PM
 */
public class ProductExpressionsImpl extends AbstractExpression implements ProductExpression {
    public ProductExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithProductExpr");
    }

    public long computeNumericValue() {
        List<ArithmeticExpression> childs = subexpressions();
        if (childs.size() == 0) {
            throw new UnsupportedOperationException("unsupported");
        }

        long result = childs.get(0).computeNumericValue();

        int i = 1;
        while (i < childs.size()) {
            ArithmeticExpression c = childs.get(i);
            long nextValue = c.computeNumericValue();

            PsiElement opElement = c;
            do {
                opElement = opElement.getPrevSibling();
            } while (opElement != null && opElement.getNode().getElementType() == BashTokenTypes.WHITESPACE);

            if (opElement != null) {
                IElementType op = opElement.getNode().getElementType();

                if (op == BashTokenTypes.ARITH_MULT) {
                    result *= nextValue;
                } else if (op == BashTokenTypes.ARITH_DIV) {
                    result /= nextValue;
                }
            }

            i++;
        }

        return result;
    }

}