/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashParameterExpansionImpl.java, Class: BashParameterExpansionImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashParameterExpansion;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiStubElement;
import com.intellij.lang.ASTNode;

/**
 * A Bash parameter expansion container may contain one or more variables.
 * <p/>
 * User: jansorg
 * Date: Nov 7, 2009
 * Time: 1:03:28 PM
 */
public class BashParameterExpansionImpl extends BashPsiStubElement implements BashParameterExpansion {
    public BashParameterExpansionImpl(final ASTNode astNode) {
        super(astNode, "Parameter expansion");
    }
}
