/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractExpression.java, Class: AbstractExpression
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
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for arithmetic expressions.
 * <p/>
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 12:14:33 PM
 */
public abstract class AbstractExpression extends BashPsiElementImpl implements ArithmeticExpression {
    private final Type type;

    public enum Type {
        NoOperands,
        TwoOperands,
        PrefixOperand,
        PostfixOperand,
        Unsupported
    }

    public AbstractExpression(final ASTNode astNode, final String name, Type type) {
        super(astNode, name);
        this.type = type;
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitArithmeticExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public boolean isStatic() {
        //fixme smaren up this implementation
        List<ArithmeticExpression> arithmeticExpressionList = subexpressions();
        for (ArithmeticExpression e : arithmeticExpressionList) {
            if (!e.isStatic()) {
                return false;
            }
        }

        return arithmeticExpressionList.size() >= 1;
    }

    public List<ArithmeticExpression> subexpressions() {
        return Arrays.asList(findChildrenByClass(ArithmeticExpression.class));
    }

    protected abstract Long compute(long currentValue, IElementType operator, Long nextExpressionValue);

    public long computeNumericValue() {
        List<ArithmeticExpression> childs = subexpressions();
        int childSize = childs.size();
        if (childSize == 0) {
            throw new UnsupportedOperationException("unsupported");
        }

        ArithmeticExpression firstChild = childs.get(0);
        long result = firstChild.computeNumericValue();

        if (type == Type.PostfixOperand) {
            PsiElement operator = BashPsiUtils.findNextSibling(firstChild, BashTokenTypes.WHITESPACE);
            return compute(result, BashPsiUtils.nodeType(operator), null);
        } else if (type == Type.PrefixOperand) {
            PsiElement operator = BashPsiUtils.findPreviousSibling(firstChild, BashTokenTypes.WHITESPACE);
            return compute(result, BashPsiUtils.nodeType(operator), null);
        } else if (type == Type.TwoOperands) {
            int i = 1;
            while (i < childSize) {
                ArithmeticExpression c = childs.get(i);
                long nextValue = c.computeNumericValue();

                PsiElement opElement = BashPsiUtils.findPreviousSibling(c, BashTokenTypes.WHITESPACE);
                if (opElement != null) {
                    IElementType op = BashPsiUtils.nodeType(opElement);

                    result = compute(result, op, nextValue);
                }

                i++;
            }

            return result;
        } else {
            throw new UnsupportedOperationException("unsupported");
        }
    }

    public ArithmeticExpression findParentExpression() {
        PsiElement context = getParent();
        if (context instanceof AbstractExpression) {
            return (AbstractExpression) context;
        }

        return null;
    }
}
