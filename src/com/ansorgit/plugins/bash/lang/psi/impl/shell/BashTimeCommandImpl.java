/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTimeCommandImpl.java, Class: BashTimeCommandImpl
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashTimeCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Date: 06.05.2009
 * Time: 13:21:38
 *
 * @author Joachim Ansorg
 */
public class BashTimeCommandImpl extends BashKeywordDefaultImpl implements BashTimeCommand {
    public BashTimeCommandImpl(final ASTNode astNode) {
        super(astNode, "bash time command");
    }

    public PsiElement keywordElement() {
        return findChildByType(BashTokenTypes.TIME_KEYWORD);
    }
}
