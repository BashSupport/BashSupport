/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ShellCommandDelegator.java, Class: ShellCommandDelegator
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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.intellij.psi.tree.IElementType;

/**
 * This simply delegates the parsing to the shellcommands. This way internal shell commands
 * are marked as commands, too.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:39:57
 *
 * @author Joachim Ansorg
 */
public class ShellCommandDelegator extends DefaultParsingFunction {
    public boolean isValid(IElementType token) {
        throw new IllegalStateException("isValid(token) not support");
    }

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return Parsing.shellCommand.isValid(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        final boolean ok = Parsing.shellCommand.parse(builder);
        //parse optional redirect list, if the shell command parsed
        final boolean redirectOk = Parsing.redirection.parseList(builder, true)
                || !ok
                || !Parsing.redirection.isRedirect(builder);
        return ok && redirectOk;
    }
}
