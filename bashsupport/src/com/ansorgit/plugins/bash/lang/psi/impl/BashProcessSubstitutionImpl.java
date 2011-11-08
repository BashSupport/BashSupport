/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashProcessSubstitutionImpl.java, Class: BashProcessSubstitutionImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashProcessSubstitution;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: 13.07.2010
 * Time: 18:54:48
 */
public class BashProcessSubstitutionImpl extends BashPsiElementImpl implements BashProcessSubstitution {
    public BashProcessSubstitutionImpl(final ASTNode astNode) {
        super(astNode, "process substitution element");
    }
}
