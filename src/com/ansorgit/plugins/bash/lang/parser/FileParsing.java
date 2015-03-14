/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FileParsing.java, Class: FileParsing
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;

/**
 * Parsing of Bash files. This is the entry to parse full bash scripts.
 * <p/>
 * Date: 24.03.2009
 * Time: 20:36:37
 *
 * @author Joachim Ansorg
 */
public class FileParsing implements ParsingTool {
    public boolean parseFile(BashPsiBuilder builder) {
        builder.eatOptionalNewlines();
        if (builder.getTokenType() == SHEBANG) {
            ParserUtil.markTokenAndAdvance(builder, SHEBANG_ELEMENT);
        }

        builder.remapShebangToComment();

        boolean success = true;
        while (!builder.eof()) {
            builder.eatOptionalNewlines();
            //fixme calling twice to work around a yet undiscovered bug
            //for some reason the newlines are not eaten at the beginning after the shebang line
            builder.eatOptionalNewlines();
            if (builder.eof()) {
                break;
            }

            final boolean ok = Parsing.list.parseSimpleList(builder);

            if (!builder.eof() && Parsing.list.isSimpleListTerminator(builder.getTokenType())) {
                builder.advanceLexer();
            }

            if (!ok && !builder.eof()) {
                builder.advanceLexer();
                success = false;
            }
        }

        return success;
    }
}
