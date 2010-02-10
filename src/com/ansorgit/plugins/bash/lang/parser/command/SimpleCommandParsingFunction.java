/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: SimpleCommandParsingFunction.java, Class: SimpleCommandParsingFunction
 * Last modified: 2010-02-10
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
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;

/**
 * Parses a simple command. A simple command is a combination of assignments, redirects,
 * a command and parameters.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:33:30
 *
 * @author Joachim Ansorg
 */
public class SimpleCommandParsingFunction extends DefaultParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        return isSimpleCommandElement(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        final PsiBuilder.Marker cmdMarker = builder.mark();

        //read assignments and redirects
        final boolean hasAssignmentOrRedirect = CommandParsingUtil.readAssignmentsAndRedirects(builder, true, CommandParsingUtil.Mode.StrictAssignmentMode);

        //read the command word
        final boolean hasCommand = parseCommandWord(builder);
        if (hasCommand) {
            //read the params and redirects
            boolean paramsAreFine = CommandParsingUtil.readCommandParams(builder);
            if (!paramsAreFine) {
                cmdMarker.drop();
                return false;
            }
        } else if (!hasAssignmentOrRedirect) {
            ParserUtil.error(cmdMarker, "parser.command.expected.command");
            return false;
        }

        cmdMarker.done(SIMPLE_COMMAND_ELEMENT);

        return true;
    }

    /**
     * Parses the command word of a simple command. A command has only to be present
     * if no other parts are there,i.e. assignments or redirect.
     *
     * @param builder The builder to use
     * @return True if the command has been parsed successfully.
     */
    private boolean parseCommandWord(BashPsiBuilder builder) {
        boolean isWord = Parsing.word.isWordToken(builder);
        if (!isWord) {
            return false;
        }

        final PsiBuilder.Marker cmdMarker = builder.mark();
        final boolean internal = builder.getTokenType() == INTERNAL_COMMAND;

        if (!Parsing.word.parseWord(builder)) {
            cmdMarker.drop();
            return false;
        }

        if (internal) {
            cmdMarker.done(INTERNAL_COMMAND_ELEMENT);
        } else {
            cmdMarker.done(GENERIC_COMMAND_ELEMENT);
        }

        return true;
    }


    public boolean isSimpleCommandElement(BashPsiBuilder builder) {
        //   simple_command_element 	:	word | assignment_word | redirection;
        return Parsing.word.isWordToken(builder)
                || Parsing.redirection.isRedirect(builder)
                || Parsing.braceExpansionParsing.isValid(builder)
                || CommandParsingUtil.isAssignment(builder, CommandParsingUtil.Mode.StrictAssignmentMode);
    }

}
