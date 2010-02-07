/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: PostIncrementExpressionsImpl.java, Class: PostIncrementExpressionsImpl
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

import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ProductExpression;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:13:49 PM
 */
public class PostIncrementExpressionsImpl extends AbstractExpression implements ProductExpression {
    public PostIncrementExpressionsImpl(final ASTNode astNode) {
        super(astNode, "ArithPostIncrement", Type.Unsupported);
    }

    @Override
    protected Long compute(long currentValue, IElementType operator, Long nextExpressionValue) {
        throw new UnsupportedOperationException("unsupported");
    }
}