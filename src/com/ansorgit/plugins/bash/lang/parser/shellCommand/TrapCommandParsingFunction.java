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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.*;
import com.google.common.collect.Sets;
import com.intellij.lang.PsiBuilder;

import java.util.Set;

/**
 * Parsing of the trap command.
 * The syntax is (as documented in "help trap"): <code>trap [-lp] [arg signal_spec ...]</code>
 */
public class TrapCommandParsingFunction implements ParsingFunction {
    private static final Set<String> allowedParams = Sets.newHashSet("-l", "-p", "-lp", "-pl");

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == TRAP_KEYWORD;
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        //trap keyword
        builder.advanceLexer();

        boolean listParam = false; //presence of -l
        boolean printParam = false; //presence of -p

        while (builder.eof() && allowedParams.contains(builder.getTokenText())) {
            String tokenText = builder.getTokenText();
            if (tokenText.contains("-l")) {
                listParam = true;
            }

            if (builder.getTokenText().contains("-p")) {
                printParam = true;
            }

            builder.advanceLexer();
        }

        boolean success = true;
        //if -l is given then trap lists all supported signal names, it overrides all other params

        //if -p is given then trap prints either all handlers or the handlers for the optional signal handler

        if (!listParam && !printParam && !builder.eof()) {
            //the next token is the name of a command to attach the signal spec to
            //we need to nest the elements in the same way as BashSimpleCommandImpl
            //fixme improve this handling, if possible

            //if the handler is an unquoted string, then we wrap it in the same structure as a simple command
            //if it is a quoted string, then we need to parse the code like eval does
            if (builder.getTokenType() == BashTokenTypes.WORD) {
                PsiBuilder.Marker commandMarker = builder.mark();
                PsiBuilder.Marker innerCommandMarker = builder.mark();

                builder.advanceLexer();

                innerCommandMarker.done(GENERIC_COMMAND_ELEMENT);
                commandMarker.done(SIMPLE_COMMAND_ELEMENT);
            } else if (builder.getTokenType() == STRING2 || Parsing.word.isComposedString(builder.getTokenType())) {
                //eval parsing
                PsiBuilder.Marker evalMarker = builder.mark();

                boolean emptyBlock;
                if (builder.getTokenType() == STRING2) {
                    emptyBlock = builder.getTokenText().length() <= 2;
                    builder.advanceLexer();
                    success = true;
                } else {
                    emptyBlock = builder.rawLookup(1) == BashTokenTypes.STRING_END || builder.rawLookup(1) == null;
                    success = Parsing.word.parseComposedString(builder);
                }

                if (success && !emptyBlock) {
                    evalMarker.collapse(BashElementTypes.EVAL_BLOCK);
                } else {
                    evalMarker.drop();
                }
            } else {
                builder.error("Expected function or code block");
            }
        }

        if (success) {
            OptionalParseResult result = Parsing.word.parseWordListIfValid(builder, false, true);
            if (result.isValid()) {
                success = result.isParsedSuccessfully();
            }
        }

        if (success) {
            marker.done(TRAP_COMMAND);
        } else {
            marker.drop();
        }

        return success;
    }
}
