/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashRedirectListImpl.java, Class: BashRedirectListImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.expression;

import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: Oct 29, 2009
 * Time: 8:51:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class BashRedirectListImpl extends BashPsiElementImpl implements BashRedirectList {
    public BashRedirectListImpl(final ASTNode astNode) {
        super(astNode, "BashRedirectList");
    }
}
