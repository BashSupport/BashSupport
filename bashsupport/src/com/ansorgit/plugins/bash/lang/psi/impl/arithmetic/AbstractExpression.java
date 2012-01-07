/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractExpression.java, Class: AbstractExpression
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
    private Boolean isStatic = null;

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
        if (isStatic == null) {
            //fixme smarten up this implementation
            List<ArithmeticExpression> arithmeticExpressionList = subexpressions();

            for (ArithmeticExpression e : arithmeticExpressionList) {
                if (!e.isStatic()) {
                    isStatic = false;
                    break;
                }
            }

            if (isStatic == null) {
                isStatic = arithmeticExpressionList.size() >= 1;
            }
        }

        return isStatic;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        this.isStatic = null;
    }

    public List<ArithmeticExpression> subexpressions() {
        //fixme cache this?
        return Arrays.asList(findChildrenByClass(ArithmeticExpression.class));
    }

    protected abstract Long compute(long currentValue, IElementType operator, Long nextExpressionValue);

    public long computeNumericValue() {
        List<ArithmeticExpression> childs = subexpressions();
        int childSize = childs.size();
        if (childSize == 0) {
            throw new UnsupportedOperationException("unsupported, zero children are not supported");
        }

        ArithmeticExpression firstChild = childs.get(0);
        long result = firstChild.computeNumericValue();

        if (type == Type.PostfixOperand || type == Type.PrefixOperand) {
            return compute(result, findOperator(), null);
        } else if (type == Type.TwoOperands) {
            int i = 1;
            while (i < childSize) {
                ArithmeticExpression c = childs.get(i);
                long nextValue = c.computeNumericValue();

                PsiElement opElement = BashPsiUtils.findPreviousSibling(c, BashTokenTypes.WHITESPACE);
                if (opElement != null) {
                    IElementType operator = BashPsiUtils.nodeType(opElement);

                    result = compute(result, operator, nextValue);
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
        if (context instanceof ArithmeticExpression) {
            return (ArithmeticExpression) context;
        }

        return null;
    }

    /**
     * Find the first operator which belongs to this expression.
     *
     * @return The operator, if available. Null otherwise.
     */
    public IElementType findOperator() {
        return BashPsiUtils.nodeType(findOperatorElement());
    }

    @Override
    public PsiElement findOperatorElement() {
        List<ArithmeticExpression> childs = subexpressions();
        int childSize = childs.size();
        if (childSize == 0) {
            return null;
        }

        ArithmeticExpression firstChild = childs.get(0);

        if (type == Type.PostfixOperand) {
            return BashPsiUtils.findNextSibling(firstChild, BashTokenTypes.WHITESPACE);
        } else if (type == Type.PrefixOperand) {
            return BashPsiUtils.findPreviousSibling(firstChild, BashTokenTypes.WHITESPACE);
        } else if (type == Type.TwoOperands) {
            int i = 1;
            while (i < childSize) {
                PsiElement opElement = BashPsiUtils.findPreviousSibling(childs.get(i), BashTokenTypes.WHITESPACE);
                if (opElement != null) {
                    //found
                    return opElement;
                }

                i++;
            }
        }

        return null;
    }


}
