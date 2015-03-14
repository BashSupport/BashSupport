/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AssignmentExpressionsImpl.java, Class: AssignmentExpressionsImpl
 * Last modified: 2010-07-17
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

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.AssignmentExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:13:49 PM
 */
public class AssignmentExpressionsImpl extends AbstractExpression implements AssignmentExpression {
    public AssignmentExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithmeticAssignmentChain", Type.Unsupported);
    }

    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new UnsupportedOperationException("compute is not unsupported");
    }

    @Override
    public long computeNumericValue() {
        //the value is the value of the last assignment chain part
        PsiElement child = getLastChild();
        if (child instanceof ArithmeticExpression) {
            return ((ArithmeticExpression) child).computeNumericValue();
        }

        throw new IllegalStateException("computeNumericValue is not supported in this configuration");
    }
}