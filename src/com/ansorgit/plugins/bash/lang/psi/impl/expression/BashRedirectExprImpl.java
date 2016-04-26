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

package com.ansorgit.plugins.bash.lang.psi.impl.expression;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectExpr;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseStubElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public class BashRedirectExprImpl extends BashBaseElement implements BashRedirectExpr {
    public BashRedirectExprImpl(final ASTNode astNode) {
        super(astNode, "BashRedirectExpr");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitRedirectExpression(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
