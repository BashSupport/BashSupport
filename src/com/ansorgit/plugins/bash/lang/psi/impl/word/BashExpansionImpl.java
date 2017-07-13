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

package com.ansorgit.plugins.bash.lang.psi.impl.word;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.ansorgit.plugins.bash.lang.valueExpansion.ValueExpansionUtil;
import com.ansorgit.plugins.bash.settings.BashProjectSettings;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * @author jansorg
 */
public class BashExpansionImpl extends BashBaseElement implements BashExpansion {
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

    @Override
    public boolean isWrapped() {
        return false;
    }

    @Override
    public String createEquallyWrappedString(String newContent) {
        return getText();
    }

    public String getUnwrappedCharSequence() {
        return getText();
    }

    public boolean isStatic() {
        return true;
    }

    @NotNull
    public TextRange getTextContentRange() {
        return TextRange.from(0, getTextLength());
    }

    public boolean isWrappable() {
        return false;
    }

    public boolean isValidExpansion() {
        return ValueExpansionUtil.isValid(getText(), BashProjectSettings.storedSettings(getProject()).isSupportBash4());
    }
}
