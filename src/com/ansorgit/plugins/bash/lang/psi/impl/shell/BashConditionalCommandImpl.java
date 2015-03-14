/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashConditionalCommandImpl.java, Class: BashConditionalCommandImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl.shell;

import com.ansorgit.plugins.bash.lang.psi.api.shell.BashConditionalCommand;
import com.ansorgit.plugins.bash.lang.psi.impl.BashPsiElementImpl;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: 06.08.2009
 * Time: 22:43:07
 */
public class BashConditionalCommandImpl extends BashPsiElementImpl implements BashConditionalCommand {
    public BashConditionalCommandImpl(final ASTNode astNode) {
        super(astNode, "Bash conditional command");
    }
}
