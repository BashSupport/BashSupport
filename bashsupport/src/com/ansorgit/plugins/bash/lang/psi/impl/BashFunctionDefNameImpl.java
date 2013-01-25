/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctionDefNameImpl.java, Class: BashFunctionDefNameImpl
 * Last modified: 2013-01-25
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 12.04.2009
 * Time: 20:40:29
 *
 * @author Joachim Ansorg
 */
public class BashFunctionDefNameImpl extends BashPsiElementImpl implements BashFunctionDefName {
    public BashFunctionDefNameImpl(ASTNode astNode) {
        super(astNode, "BashFunctionDefName");
    }

    @NotNull
    public String getNameString() {
        return getText();
    }
}
