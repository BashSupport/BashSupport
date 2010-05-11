/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: TernaryExpressionsImpl.java, Class: TernaryExpressionsImpl
 * Last modified: 2010-05-11
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
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.TernaryExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:13:49 PM
 */
public class TernaryExpressionsImpl extends AbstractExpression implements TernaryExpression {
    public TernaryExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithTernaryExpr", Type.Unsupported);
    }

    @NotNull
    public ArithmeticExpression findCondition() {
        ArithmeticExpression[] firstChild = findChildrenByClass(ArithmeticExpression.class);

        return firstChild[0];
    }

    @NotNull
    public ArithmeticExpression findMainBranch() {
        ArithmeticExpression[] firstChild = findChildrenByClass(ArithmeticExpression.class);

        return firstChild[1];
    }

    @NotNull
    public ArithmeticExpression findElseBranch() {
        ArithmeticExpression[] firstChild = findChildrenByClass(ArithmeticExpression.class);

        return firstChild[2];
    }

    @Override
    public boolean isStatic() {
        ArithmeticExpression condition = findCondition();
        ArithmeticExpression mainBranch = findMainBranch();
        ArithmeticExpression elseBranch = findElseBranch();

        if (condition.isStatic()) {
            return condition.computeNumericValue() != 0 ? mainBranch.isStatic() : elseBranch.isStatic();
        }

        //although the condition may not be static the expression
        //is still static if both branches are static and evaluate to the same numeric result
        return mainBranch.isStatic()
                && elseBranch.isStatic()
                && mainBranch.computeNumericValue() == elseBranch.computeNumericValue();

    }

    @Override
    public long computeNumericValue() {
        ArithmeticExpression condition = findCondition();

        if (condition.isStatic()) {
            if (condition.computeNumericValue() != 0) {
                return findMainBranch().computeNumericValue();
            } else {
                return findElseBranch().computeNumericValue();
            }
        }

        //in this case we assume that both branche's values are equal
        return findMainBranch().computeNumericValue();
    }

    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new UnsupportedOperationException("compute is unsupported in a ternary expression");
    }
}