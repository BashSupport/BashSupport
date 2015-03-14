/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSymbolImpl.java, Class: BashSymbolImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.lang.psi.api.BashSymbol;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 12.04.2009
 * Time: 20:40:29
 *
 * @author Joachim Ansorg
 */
public class BashSymbolImpl extends BashPsiElementImpl implements BashSymbol {
    public BashSymbolImpl(ASTNode astNode) {
        super(astNode, "BashSymbol");
    }

    @NotNull
    public String getNameString() {
        return getText();
    }
}
