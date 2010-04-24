/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: LoopParserUtil.java, Class: LoopParserUtil
 * Last modified: 2010-04-24
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.psi.tree.IElementType;

/**
 * Helper class to provide parsing of a loop body. This code is shared between while and
 * until loops.
 * <p/>
 * Date: 02.05.2009
 * Time: 17:51:04
 *
 * @author Joachim Ansorg
 */
class LoopParserUtil implements BashTokenTypes {
    /**
     * Parses a compound list enclodes by {} or by DO ..  DONE
     * This type of command block is used in for and select loops.
     *
     * @param builder           The builder which provides the data.
     * @param parseCompoundList True if a compound list is expected as loop body. False if a normal list is expteced.
     */
    boolean parseLoopBody(BashPsiBuilder builder, boolean parseCompoundList) {
        final IElementType firstToken = ParserUtil.getTokenAndAdvance(builder);
        if (firstToken == DO_KEYWORD || firstToken == LEFT_CURLY) {
            boolean parsed = parseCompoundList
                    ? Parsing.list.parseCompoundList(builder, true)
                    : Parsing.list.parseList(builder);

            if (parsed) {
                final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
                boolean ok = (firstToken == DO_KEYWORD && lastToken == DONE_KEYWORD)
                        || (firstToken == LEFT_CURLY && lastToken == RIGHT_CURLY);

                if (!ok) {
                    ParserUtil.error(builder, "parser.unexpected.token");
                }

                return ok;
            } else {
                //ParserUtil.error(builder, "parser.shell.loop.expectedBody");
                return false;
            }
        }

        return false;
    }

}
