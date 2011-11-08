/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCasePatternImpl.java, Class: BashCasePatternImpl
 * Last modified: 2010-02-06 10:49
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

package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.psi.api.shell.BashCasePattern;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: Oct 29, 2009
 * Time: 8:46:18 PM
 */
public class BashCasePatternImpl extends BashPsiElementImpl implements BashCasePattern {
    public BashCasePatternImpl(final ASTNode astNode) {
        super(astNode, "BashCasePattern");
    }
}
