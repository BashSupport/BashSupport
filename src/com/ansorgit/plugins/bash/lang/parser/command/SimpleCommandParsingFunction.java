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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.intellij.lang.PsiBuilder;

/**
 * Parses a simple command. A simple command is a combination of assignments, redirects, a command and parameters.
 *
 * @author jansorg
 */
class SimpleCommandParsingFunction implements ParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        throw new UnsupportedOperationException("call parseIfValid() instead");
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        return parseIfValid(builder).isParsedSuccessfully();
    }

    @Override
    public OptionalParseResult parseIfValid(BashPsiBuilder builder) {
        final PsiBuilder.Marker cmdMarker = builder.mark();

        //read assignments and redirects
        final boolean hasAssignmentOrRedirect = CommandParsingUtil.readAssignmentsAndRedirectsIfValid(builder, true, CommandParsingUtil.Mode.StrictAssignmentMode, true).isParsedSuccessfully();

        final boolean hasCommand = parseCommandWord(builder);
        if (hasCommand) {
            //read the params and redirects
            boolean parsedParams = CommandParsingUtil.readCommandParams(builder);
            if (!parsedParams) {
                cmdMarker.drop();
                return OptionalParseResult.ParseError;
            }
        } else if (!hasAssignmentOrRedirect) {
            cmdMarker.drop();
            return OptionalParseResult.Invalid;
        }

        cmdMarker.done(SIMPLE_COMMAND_ELEMENT);
        return OptionalParseResult.Ok;
    }

    /**
     * Parses the command word of a simple command. A command has only to be present
     * if no other parts are there, i.e. assignments or redirect.
     *
     * @param builder The builder to use
     * @return True if the command has been parsed successfully.
     */
    private boolean parseCommandWord(BashPsiBuilder builder) {
        final PsiBuilder.Marker cmdMarker = builder.mark();

        if (!Parsing.word.parseWordIfValid(builder, false).isParsedSuccessfully()) {
            cmdMarker.drop();
            return false;
        }

        cmdMarker.done(GENERIC_COMMAND_ELEMENT);
        return true;
    }
}
