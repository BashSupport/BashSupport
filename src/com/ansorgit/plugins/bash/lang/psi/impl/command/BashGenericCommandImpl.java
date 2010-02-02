/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashGenericCommandImpl.java, Class: BashGenericCommandImpl
 * Last modified: 2010-01-25
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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: Oct 29, 2009
 * Time: 8:19:49 PM
 */
public class BashGenericCommandImpl extends BashCommandImpl implements BashGenericCommand {
    public BashGenericCommandImpl(ASTNode astNode) {
        super(astNode, "BashGenericCommand");
    }
}