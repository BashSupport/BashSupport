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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Abstract base class for loops.
 * <br>
 * @author jansorg
 */
public class AbstractLoopParser implements ParsingTool, ParsingFunction {
    private final IElementType startToken;
    private final IElementType commandMarker;

    public AbstractLoopParser(IElementType startToken, IElementType commandMarker) {
        this.startToken = startToken;
        this.commandMarker = commandMarker;
    }

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == startToken;
    }

    public boolean parse(BashPsiBuilder builder) {
        /*  Grammar:
            WHILE compound_list DO compound_list DONE
            UNTIL compound_list DO compound_list DONE
         */

        final PsiBuilder.Marker loopMarker = builder.mark();
        builder.advanceLexer();

        if (ParserUtil.isEmptyListFollowedBy(builder, DO_KEYWORD)) {
            ParserUtil.error(builder, "parser.shell.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, DO_KEYWORD);
        } else if (!Parsing.list.parseCompoundList(builder, false)) {
            loopMarker.drop();
            return false;
        }

        if (!ParserUtil.checkNextOrError(builder, loopMarker, ShellCommandParsing.DO_KEYWORD, "parser.shell.expectedDo")) {
            return false;
        }


        if (ParserUtil.isEmptyListFollowedBy(builder, DONE_KEYWORD)) {
            ParserUtil.error(builder, "parser.shell.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, DONE_KEYWORD);
        } else if (!Parsing.list.parseCompoundList(builder, true)) {
            loopMarker.drop();
            return false;
        }

        if (!ParserUtil.checkNextOrError(builder, loopMarker, ShellCommandParsing.DONE_KEYWORD, "parser.shell.expectedDone")) {
            return false;
        }

        loopMarker.done(commandMarker);
        return true;
    }
}
