/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BacktickParsingFunction.java, Class: BacktickParsingFunction
 * Last modified: 2010-06-05
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
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for backtic / backquote calls.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:17:08
 *
 * @author Joachim Ansorg
 */
public class BacktickParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.BackquoteParsing");

    public boolean isValid(BashPsiBuilder builder) {
        return !builder.getBackquoteData().isInBackquote() && builder.getTokenType() == BashTokenTypes.BACKQUOTE;
    }

    public boolean parse(BashPsiBuilder builder) {
        /*
          backquote: '`' compound_list '`'
         */
        log.assertTrue(isValid(builder));

        final PsiBuilder.Marker backquote = builder.mark();
        builder.advanceLexer(); //after the initial backquote
        builder.getBackquoteData().enterBackquote();

        try {
            final boolean empty = builder.getTokenType() == BashTokenTypes.BACKQUOTE;

            //parse compound list
            if (!empty) {
                if (!Parsing.list.parseCompoundList(builder, true, false)) {
                    ParserUtil.error(backquote, "parser.shell.expectedCommands");
                    return false;
                }
            }

            //get and check end token
            final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
            if (lastToken != BashTokenTypes.BACKQUOTE) {
                ParserUtil.error(backquote, "parser.unexpected.token");
                return false;
            }

            backquote.done(BashElementTypes.BACKQUOTE_COMMAND);
            return true;
        } finally {
            builder.getBackquoteData().leaveBackquote();
        }
    }
}
