/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFiledescriptorImpl.java, Class: BashFiledescriptorImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.expression;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashFiledescriptor;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: jansorg
 * Date: 10.05.2010
 * Time: 19:57:38
 */
public class BashFiledescriptorImpl extends BashPsiElementImpl implements BashFiledescriptor {
    public BashFiledescriptorImpl(ASTNode astNode) {
        super(astNode, "Bash filedescriptor");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitFiledescriptor(this);
        } else {
            visitor.visitElement(this);
        }
    }

    @Nullable
    public Integer descriptorAsInt() {
        String text = getText();
        if (!(text.length() > 0 && text.charAt(0) == '&') || text.equals("&-")) {
            return null;
        }

        try {
            return Integer.valueOf(text.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
