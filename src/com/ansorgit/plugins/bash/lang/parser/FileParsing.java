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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing of Bash files. This is the entry to parse full bash scripts.
 * <br>
 *
 * @author jansorg
 */
public class FileParsing implements ParsingTool {
    public boolean parseFile(BashPsiBuilder builder) {
        builder.readOptionalNewlines();
        if (builder.getTokenType() == SHEBANG) {
            ParserUtil.markTokenAndAdvance(builder, SHEBANG_ELEMENT);
        }

        builder.remapShebangToComment();

        boolean success = true;
        while (!builder.eof()) {
            builder.readOptionalNewlines();
            //fixme calling twice to work around a yet undiscovered bug
            //for some reason the newlines are not eaten at the beginning after the shebang line
            builder.readOptionalNewlines();
            if (builder.eof()) {
                break;
            }

            boolean toplevelExitCommand = isToplevelExit(builder);

            boolean ok = Parsing.list.parseSimpleList(builder);
            toplevelExitCommand &= ok;

            if (!builder.eof() && Parsing.list.isSimpleListTerminator(builder.getTokenType())) {
                builder.advanceLexer();
            }

            //if the command was exit and is on top-level context then all further tokens are read as binary data
            if (toplevelExitCommand) {
                PsiBuilder.Marker binaryMarker = builder.mark();

                while (!builder.eof()) {
                    builder.advanceLexer();
                }

                binaryMarker.done(BashElementTypes.BINARY_DATA);
            }

            if (!ok && !builder.eof()) {
                builder.advanceLexer();
                success = false;
            }
        }

        return success;
    }

    private boolean isToplevelExit(BashPsiBuilder builder) {
        if (builder.getTokenType() != WORD || !"exit".equals(builder.getTokenText())) {
            return false;
        }

        IElementType prev = builder.rawLookup(-1);
        if (prev != null && prev != LINE_FEED && prev != SHEBANG) {
            return false;
        }

        //exit followed by whitespace
        IElementType type = builder.lookAhead(1);
        if (type != null && type != WORD && type != STRING2 && type != INTEGER_LITERAL && type != LINE_FEED) {
            return false;
        }

        return true;
    }
}
