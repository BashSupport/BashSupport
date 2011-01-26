/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: IfParsingFunction.java, Class: IfParsingFunction
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
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for if expressions.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:24:54
 *
 * @author Joachim Ansorg
 */
public class IfParsingFunction implements ParsingFunction {

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == BashTokenTypes.IF_KEYWORD;
    }

    public boolean parse(BashPsiBuilder builder) {
        /*
            if_command 	:
                IF compound_list THEN compound_list FI
            |	IF compound_list THEN compound_list ELSE compound_list FI
            |	IF compound_list THEN compound_list elif_clause FI
            ;
         */

        final PsiBuilder.Marker ifCommand = builder.mark();
        final IElementType tokenType = builder.getTokenType();

        if (tokenType != BashTokenTypes.IF_KEYWORD) {
            ParserUtil.error(ifCommand, "parser.shell.if.expectedIf");//fixme
            return false;
        }

        builder.advanceLexer();

        if (!Parsing.list.parseCompoundList(builder, false)) {
            ParserUtil.error(builder, "parser.shell.if.expectedCommands");
            ifCommand.drop();
            return false;
        }

        final IElementType thenKeyword = ParserUtil.getTokenAndAdvance(builder);
        if (thenKeyword != BashTokenTypes.THEN_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedThen");
            ifCommand.drop();
            return false;
        }

        if (!Parsing.list.parseCompoundList(builder, true)) {
            ifCommand.drop();
            return false;
        }

        if (builder.getTokenType() == BashTokenTypes.ELIF_KEYWORD) {
            if (!parseElifClause(builder)) {
                ifCommand.drop();//fixme
                return false;
            }
        }

        if (builder.getTokenType() == BashTokenTypes.ELSE_KEYWORD) {
            builder.advanceLexer();
            //after the else keyword now

            if (!Parsing.list.parseCompoundList(builder, true)) {
                //ParserUtil.error(ifCommand, "parser.shell.if.expectedCommands");
                ifCommand.drop();
                return false;
            }
        }

        final IElementType fiKeyword = ParserUtil.getTokenAndAdvance(builder);
        if (fiKeyword != BashTokenTypes.FI_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedFi");
            ifCommand.drop();
            return false;
        }

        ifCommand.done(BashElementTypes.IF_COMMAND);
        return true;
    }

    private boolean parseElifClause(BashPsiBuilder builder) {
        /*
          elif_clause:
            ELIF compound_list THEN compound_list
        |   ELIF compound_list THEN compound_list ELSE compound_list
        |   ELIF compound_list THEN compound_list elif_clause
        ;
       */

        final IElementType token = ParserUtil.getTokenAndAdvance(builder);
        if (token != BashTokenTypes.ELIF_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedElif");//fixme
            return false;
        }

        if (!Parsing.list.parseCompoundList(builder, false)) {
            ParserUtil.error(builder, "parser.command.expected.command");
            return false;
        }

        final IElementType thenToken = ParserUtil.getTokenAndAdvance(builder);
        if (thenToken != BashTokenTypes.THEN_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedThen");
            return false;
        }

        //commands after "then"
        if (!Parsing.list.parseCompoundList(builder, true)) {
            ParserUtil.error(builder, "parser.command.expected.command");
            return false;
        }

        if (builder.getTokenType() == BashTokenTypes.ELIF_KEYWORD) {
            return parseElifClause(builder);
        }

        //the else is handled by the parseIfCommand method
        return true;
    }
}
