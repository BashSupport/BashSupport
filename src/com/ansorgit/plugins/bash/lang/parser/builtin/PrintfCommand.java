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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.parser.*;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Syntax: printf: printf [-v var] Format [Argumente]
 * Returns success unless an invalid option is given or a write or
 * assignment error occurs.<
 *
 * @author jansorg
 */
class PrintfCommand implements ParsingFunction, ParsingTool {

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == WORD && "printf".equals(builder.getTokenText());
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker cmdMarker = builder.mark();

        //read local-cmd vars
        OptionalParseResult result = CommandParsingUtil.readAssignmentsAndRedirectsIfValid(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode, false);
        if (result.isValid() && !result.isParsedSuccessfully()) {
            cmdMarker.drop();
            return false;
        }

        //cmd word
        PsiBuilder.Marker cmdWord = builder.mark();
        builder.advanceLexer();
        cmdWord.done(GENERIC_COMMAND_ELEMENT);

        // -v has to be the first argument
        if ("-v".equals(builder.getTokenText())) {
            builder.advanceLexer();

            // check for the var name text token
            if (Parsing.word.isWordToken(builder)) {
                if (!parseVariableName(builder)) {
                    cmdMarker.drop();
                    return false;
                }
            } else {
                cmdMarker.drop();
                builder.error("Expected variable name");
                return false;
            }
        }

        //read all remaining parameters
        //fixme mark the format string as printf value?

        CommandParsingUtil.readCommandParams(builder);

        cmdMarker.done(SIMPLE_COMMAND_ELEMENT);
        return true;
    }

    private boolean parseVariableName(BashPsiBuilder builder) {
        IElementType token = builder.getTokenType();
        if (token == WORD || token == STRING2 || Parsing.word.isSimpleComposedString(builder, false)) {
            return parseSimpleWord(builder);
        }

        OptionalParseResult result = Parsing.word.parseWordIfValid(builder);
        if (result.isValid()) {
            return result.isParsedSuccessfully();
        }

        return false;
    }

    private boolean parseSimpleWord(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        if (Parsing.word.parseWordIfValid(builder).isParsedSuccessfully()) {
            marker.done(VAR_DEF_ELEMENT);
            return true;
        }

        marker.drop();
        return false;
    }
}
