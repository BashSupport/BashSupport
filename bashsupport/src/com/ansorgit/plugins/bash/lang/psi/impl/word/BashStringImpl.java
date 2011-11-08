/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStringImpl.java, Class: BashStringImpl
 * Last modified: 2011-02-18 20:22
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

package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashCharSequence;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * A string spanning start and end markers and content elements.
 * <p/>
 * Date: 12.04.2009
 * Time: 13:13:15
 *
 * @author Joachim Ansorg
 */
public class BashStringImpl extends BashPsiElementImpl implements BashString, BashCharSequence {
    public BashStringImpl(ASTNode node) {
        super(node, "Bash string");
    }

    public String getUnwrappedCharSequence() {
        String text = getText();
        if (text.length() <= 2) {
            return "";
        }

        return text.substring(1, text.length() - 1);
    }

    public boolean isStatic() {
        return BashPsiUtils.isStaticWordExpr(getFirstChild());
    }

    @NotNull
    public TextRange getTextContentRange() {
        return TextRange.create(1, getTextLength() - 1);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitString(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
