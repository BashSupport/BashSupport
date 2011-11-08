/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: SubshellParsingFunction.java, Class: SubshellParsingFunction
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
 * Parsing function for subshell expressions.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:20:54
 *
 * @author Joachim Ansorg
 */
public class SubshellParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.SubshellParsingFunction");

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == BashTokenTypes.LEFT_PAREN;
    }

    public boolean parse(BashPsiBuilder builder) {
        /*
            subshell:       '(' compound_list ')'
         */

        log.assertTrue(builder.getTokenType() == BashTokenTypes.LEFT_PAREN);

        final PsiBuilder.Marker subshell = builder.mark();

        builder.advanceLexer(); //after the start

        //parse compound list
        if (!Parsing.list.parseCompoundList(builder, true, false)) {
            ParserUtil.error(builder, "parser.shell.expectedCommands");
            subshell.drop();
            return false;
        }

        //get and check end token
        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != BashTokenTypes.RIGHT_PAREN) {
            ParserUtil.error(builder, "parser.unexpected.token");
            subshell.drop();
            return false;
        }

        subshell.done(BashElementTypes.SUBSHELL_COMMAND);
        return true;
    }
}
