/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.parser.builtin.varDef;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;

/**
 * Base class which accepts a list of "read" style words as variables.
 * @author jansorg
 */
abstract class AbstractReadCommand extends AbstractVariableDefParsing {
    AbstractReadCommand(String command) {
        super(true, BashElementTypes.GENERIC_COMMAND_ELEMENT, command, false, true);
    }

    /**
     * Overridden to accept words as assignment.
     *
     * @param builder provides the tokens
     * @return True if the next token is a word and thus an assignment.
     */
    boolean isAssignment(BashPsiBuilder builder) {
        final String text = builder.getTokenText();
        return (builder.getTokenType() == WORD) && (text != null) && !text.startsWith("-");
    }
}
