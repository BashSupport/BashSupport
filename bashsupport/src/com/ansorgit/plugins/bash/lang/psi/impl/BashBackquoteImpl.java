/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashBackquoteImpl.java, Class: BashBackquoteImpl
 * Last modified: 2010-02-06 10:50
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 12.04.2009
 * Time: 13:11:14
 *
 * @author Joachim Ansorg
 */
public class BashBackquoteImpl extends BashPsiElementImpl implements BashBackquote {
    public BashBackquoteImpl(ASTNode astNode) {
        super(astNode, "bash backquote");
    }

    public String getCommandText() {
        String text = getText();
        return getText().substring(1, text.length() - 1);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitBackquoteCommand(this);
        } else {
            visitor.visitElement(this);
        }
    }
}
