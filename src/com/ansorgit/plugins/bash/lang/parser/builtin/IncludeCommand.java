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

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.*;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.google.common.collect.Sets;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;

import java.util.Set;

/**
 * @author jansorg
 */
class IncludeCommand implements ParsingFunction, ParsingTool {
    private static final Set<String> acceptedCommands = Sets.newHashSet(".", "source");
    private static final TokenSet invalidFollowups = TokenSet.create(EQ);

    public boolean isValid(BashPsiBuilder builder) {
        if (invalidFollowups.contains(builder.rawLookup(1))) {
            return false;
        }

        String tokenText = builder.getTokenText();
        return LanguageBuiltins.isInternalCommand(tokenText, builder.isBash4()) && acceptedCommands.contains(tokenText);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker commandMarker = builder.mark();

        boolean ok = CommandParsingUtil.readOptionalAssignmentOrRedirects(builder, CommandParsingUtil.Mode.StrictAssignmentMode, false, true);
        if (!ok) {
            commandMarker.drop();
            return false;
        }

        //eat the "." or "source" part
        ParserUtil.markTokenAndAdvance(builder, GENERIC_COMMAND_ELEMENT);

        //parse the file reference
        PsiBuilder.Marker fileMarker = builder.mark();
        OptionalParseResult wordResult = Parsing.word.parseWordIfValid(builder, false);
        if (!wordResult.isParsedSuccessfully()) {
            fileMarker.drop();
            commandMarker.drop();
            builder.error("Expected file name");
            return false;
        }

        fileMarker.done(FILE_REFERENCE);

        Parsing.word.parseWordListIfValid(builder, false, false);

        //optional parameters
        //fixme the include command takes optional args which are passed on as positional parameters
        ok = CommandParsingUtil.readOptionalAssignmentOrRedirects(builder, CommandParsingUtil.Mode.SimpleMode, false, false);
        if (!ok) {
            commandMarker.drop();
            return false;
        }

        commandMarker.done(INCLUDE_COMMAND_ELEMENT);

        return true;
    }
}
