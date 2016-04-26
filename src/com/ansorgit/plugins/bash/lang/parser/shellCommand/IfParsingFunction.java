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

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.*;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing function for if expressions.
 * <br>
 * @author jansorg
 */
public class IfParsingFunction implements ParsingFunction {
    private static final TokenSet ELSE_ELIF_FI = TokenSet.create(ELIF_KEYWORD, ELSE_KEYWORD, FI_KEYWORD);

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
        ParserUtil.getTokenAndAdvance(builder); //if keyword

        if (ParserUtil.isEmptyListFollowedBy(builder, THEN_KEYWORD)) {
            ParserUtil.error(builder, "parser.shell.if.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, THEN_KEYWORD);
        } else if (!Parsing.list.parseCompoundList(builder, false)) {
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

        if (ParserUtil.isEmptyListFollowedBy(builder, ELSE_ELIF_FI)) {
            ParserUtil.error(builder, "parser.shell.if.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, ELSE_ELIF_FI);
        } else if (!Parsing.list.parseCompoundList(builder, true)) {
            ifCommand.drop();
            return false;
        }

        if (builder.getTokenType() == BashTokenTypes.ELIF_KEYWORD) {
            if (ParserUtil.isEmptyListFollowedBy(builder, ELSE_KEYWORD)) {
                ParserUtil.error(builder, "parser.shell.if.expectedCommands");
                ParserUtil.readEmptyListFollowedBy(builder, ELSE_KEYWORD);
            } else if (parseElifClause(builder) == ParseResult.ERRORS) {
                ifCommand.drop();//fixme
                return false;
            }
        }

        if (builder.getTokenType() == BashTokenTypes.ELSE_KEYWORD) {
            builder.advanceLexer(); //after the else keyword now

            if (ParserUtil.isEmptyListFollowedBy(builder, FI_KEYWORD)) {
                ParserUtil.error(builder, "parser.shell.if.expectedCommands");
                ParserUtil.readEmptyListFollowedBy(builder, FI_KEYWORD);
            } else if (!Parsing.list.parseCompoundList(builder, true)) {
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

    private ParseResult parseElifClause(BashPsiBuilder builder) {
        /*
          elif_clause:
            ELIF compound_list THEN compound_list
        |   ELIF compound_list THEN compound_list ELSE compound_list
        |   ELIF compound_list THEN compound_list elif_clause
        ;
       */
        boolean withErrors = false;

        final IElementType token = ParserUtil.getTokenAndAdvance(builder);
        if (token != BashTokenTypes.ELIF_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedElif");//fixme
            return ParseResult.ERRORS;
        }

        if (ParserUtil.isEmptyListFollowedBy(builder, THEN_KEYWORD)) {
            ParserUtil.error(builder, "parser.shell.if.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, THEN_KEYWORD);
            withErrors = true;
        } else if (!Parsing.list.parseCompoundList(builder, false)) {
            ParserUtil.error(builder, "parser.command.expected.command");
            return ParseResult.ERRORS;
        }

        final IElementType thenToken = ParserUtil.getTokenAndAdvance(builder);
        if (thenToken != BashTokenTypes.THEN_KEYWORD) {
            ParserUtil.error(builder, "parser.shell.if.expectedThen");
            return ParseResult.ERRORS;
        }

        //commands after "then"
        if (ParserUtil.isEmptyListFollowedBy(builder, ELSE_ELIF_FI)) {
            ParserUtil.error(builder, "parser.shell.if.expectedCommands");
            ParserUtil.readEmptyListFollowedBy(builder, ELSE_ELIF_FI);
            withErrors = true;
        } else if (!Parsing.list.parseCompoundList(builder, true)) {
            ParserUtil.error(builder, "parser.command.expected.command");
            return ParseResult.ERRORS;
        }

        if (builder.getTokenType() == BashTokenTypes.ELIF_KEYWORD) {
            return parseElifClause(builder);
        }

        //the else is handled by the parseIfCommand method
        return withErrors ? ParseResult.ERRORS_OK : ParseResult.OK;
    }
}
