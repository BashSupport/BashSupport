/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSubshellCommandImpl.java, Class: BashSubshellCommandImpl
 * Last modified: 2010-06-30
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

package com.ansorgit.plugins.bash.lang.psi.impl.expression;

import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 21.05.2009
 * Time: 14:02:27
 */
public class BashSubshellCommandImpl extends BashPsiElementImpl implements BashSubshellCommand {
    public BashSubshellCommandImpl(final ASTNode astNode) {
        super(astNode, "bash subshell command");
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof BashVisitor) {
            ((BashVisitor) visitor).visitSubshell(this);
        } else {
            visitor.visitElement(this);
        }
    }

    public String getCommandText() {
        String text = getText();
        return text.substring(1, text.length() - 1); //getText doesn't include the $
    }
}
