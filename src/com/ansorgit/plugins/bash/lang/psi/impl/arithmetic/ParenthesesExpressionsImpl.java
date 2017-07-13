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

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ParenthesesExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * @author jansorg
 */
public class ParenthesesExpressionsImpl extends AbstractExpression implements ParenthesesExpression {
    public ParenthesesExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithParenExpr", Type.NoOperands);
    }

    @Nullable
    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public long computeNumericValue() {
        List<ArithmeticExpression> childs = subexpressions();
        if (childs.size() != 1) {
            throw new IllegalStateException("impossible state");
        }

        return childs.get(0).computeNumericValue();
    }
}