/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ProductExpressionsImplTest.java, Class: ProductExpressionsImplTest
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
import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: Apr 17, 2010
 * Time: 12:22:58 AM
 */
public class ProductExpressionsImplTest {
    @Test
    public void testCompute() {
        ProductExpressionsImpl product = createDummyNode();

        Assert.assertEquals(100L, (long) product.compute(10L, BashTokenTypes.ARITH_MULT, 10L));
        Assert.assertEquals(1000L, (long) product.compute(20L, BashTokenTypes.ARITH_MULT, 50L));

        Assert.assertEquals(1L, (long) product.compute(1L, BashTokenTypes.ARITH_MULT, 1L));
        Assert.assertEquals(1L, (long) product.compute(2L, BashTokenTypes.ARITH_DIV, 2L));
        Assert.assertEquals(2L, (long) product.compute(20L, BashTokenTypes.ARITH_DIV, 10L));

        Assert.assertEquals(0L, (long) product.compute(1L, BashTokenTypes.ARITH_DIV, 2L));
        Assert.assertEquals(0L, (long) product.compute(2L, BashTokenTypes.ARITH_DIV, 3L));
        Assert.assertEquals(0L, (long) product.compute(2L, BashTokenTypes.ARITH_DIV, 4L));
    }

    private ProductExpressionsImpl createDummyNode() {
        ASTNode astNode = new LeafPsiElement(BashTokenTypes.NUMBER, "xxx");
        ProductExpressionsImpl product = new ProductExpressionsImpl(astNode);
        return product;
    }
}
