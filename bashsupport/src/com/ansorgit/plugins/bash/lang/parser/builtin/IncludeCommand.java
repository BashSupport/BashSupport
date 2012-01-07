/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: IncludeCommand.java, Class: IncludeCommand
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.google.common.collect.Sets;
import com.intellij.lang.PsiBuilder;

import java.util.Set;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 19:37
 */
class IncludeCommand implements ParsingFunction, ParsingTool {
    private final Set<String> acceptedCommands = Sets.newHashSet(".", "source");

    public boolean isValid(BashPsiBuilder builder) {
        String tokenText = builder.getTokenText();
        return LanguageBuiltins.isInternalCommand(tokenText) && acceptedCommands.contains(tokenText);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();


        boolean ok = CommandParsingUtil.readOptionalAssignmentOrRedirects(builder, CommandParsingUtil.Mode.StrictAssignmentMode, false);
        if (!ok) {
            mark.drop();
            return false;
        }

        //eat the "." or "source" part
        builder.advanceLexer();

        //parse the file reference
        PsiBuilder.Marker fileMarker = builder.mark();
        boolean wordResult = Parsing.word.parseWord(builder, false);
        if (!wordResult) {
            fileMarker.drop();
            mark.drop();
            builder.error("Expected file name");
            return false;
        }

        fileMarker.done(FILE_REFERENCE);

        while (Parsing.word.isWordToken(builder)) {
            Parsing.word.parseWord(builder);
        }

        //optional parameters

        ok = CommandParsingUtil.readOptionalAssignmentOrRedirects(builder, CommandParsingUtil.Mode.StrictAssignmentMode, false);
        if (!ok) {
            mark.drop();
            return false;
        }

        mark.done(INCLUDE_COMMAND_ELEMENT);

        return true;
    }
}
