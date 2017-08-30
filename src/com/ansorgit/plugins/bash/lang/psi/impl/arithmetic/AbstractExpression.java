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
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiUtilCore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Base class for arithmetic expressions.
 * <br>
 *
 * @author jansorg
 */
public abstract class AbstractExpression extends BashBaseElement implements ArithmeticExpression {
    private final Type type;

    private final Object stateLock = new Object();
    private volatile Boolean isStatic = null;

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
            //no other lock is used in the callees, it's safe to synchronize around the whole calculation
            synchronized (stateLock) {
                if (isStatic == null) {
                    Iterator<ArithmeticExpression> iterator = subexpressions().iterator();

                    boolean allStatic = iterator.hasNext();
                    while (allStatic && iterator.hasNext()) {
                        allStatic = iterator.next().isStatic();
                    }

                    isStatic = allStatic;
                }
            }
        }

        return isStatic;
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();

        synchronized (stateLock) {
            this.isStatic = null;
        }
    }

    //fixme cache this?
    @NotNull
    public List<ArithmeticExpression> subexpressions() {
        if (getFirstChild() == null) {
            return Collections.emptyList();
        }

        return Arrays.asList(findChildrenByClass(ArithmeticExpression.class));
    }

    @Nullable
    protected abstract Long compute(long currentValue, IElementType operator, Long nextExpressionValue);

    @Override
    public long computeNumericValue() throws InvalidExpressionValue {
        List<ArithmeticExpression> childExpressions = subexpressions();

        int childSize = childExpressions.size();
        if (childSize == 0) {
            throw new UnsupportedOperationException("unsupported, zero children are not supported");
        }

        ArithmeticExpression firstChild = childExpressions.get(0);
        long result = firstChild.computeNumericValue();

        if (type == Type.PostfixOperand || type == Type.PrefixOperand) {
            Long computed = compute(result, findOperator(), null);
            if (computed == null) {
                throw new UnsupportedOperationException("Can't calculate value for " + getText());
            }
            return computed;
        }

        if (type == Type.TwoOperands) {
            int i = 1;
            while (i < childSize) {
                ArithmeticExpression c = childExpressions.get(i);
                long nextValue = c.computeNumericValue();

                PsiElement opElement = BashPsiUtils.findPreviousSibling(c, BashTokenTypes.WHITESPACE);
                if (opElement != null) {
                    IElementType operator = PsiUtilCore.getElementType(opElement);

                    Long computed = compute(result, operator, nextValue);
                    if (computed == null) {
                        throw new UnsupportedOperationException("Can't calculate value for " + getText());
                    }
                    result = computed;
                }

                i++;
            }

            return result;
        }

        throw new UnsupportedOperationException("unsupported computation for expression " + getText());
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
        return PsiUtilCore.getElementType(findOperatorElement());
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
        }

        if (type == Type.PrefixOperand) {
            return BashPsiUtils.findPreviousSibling(firstChild, BashTokenTypes.WHITESPACE);
        }

        if (type == Type.TwoOperands) {
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
