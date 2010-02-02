/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashExpansionImpl.java, Class: BashExpansionImpl
 * Last modified: 2010-01-27
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

package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: Nov 14, 2009
 * Time: 3:04:41 PM
 */
public class BashExpansionImpl extends BashPsiElementImpl implements BashExpansion {
    public BashExpansionImpl(ASTNode astNode) {
        super(astNode, "Bash expansion");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            BashVisitor v = (BashVisitor) visitor;
            v.visitExpansion(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public String getUnwrappedCharSequence() {
        return getText();
    }

    public boolean isStatic() {
        return true;
    }

    public boolean isWrappable() {
        return false;
    }
}
