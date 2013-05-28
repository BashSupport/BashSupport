/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashForImpl.java, Class: BashForImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.loops;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.loops.BashFor;
import com.ansorgit.plugins.bash.lang.psi.impl.BashKeywordDefaultImpl;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Date: 11.04.2009
 * Time: 23:44:49
 *
 * @author Joachim Ansorg
 */
public class BashForImpl extends BashKeywordDefaultImpl implements BashFor {
    public BashForImpl() {
        super(BashElementTypes.FOR_COMMAND);
    }

    public PsiElement keywordElement() {
        return findPsiChildByType(BashTokenTypes.FOR_KEYWORD);
    }

    public boolean isArithmetic() {
        return findPsiChildByType(BashElementTypes.ARITHMETIC_COMMAND) != null;
    }
}
