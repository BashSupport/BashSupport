/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionDefParsingFunction.java, Class: FunctionDefParsingFunction
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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for function definitions.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:32:25
 *
 * @author Joachim Ansorg
 */
public class FunctionDefParsingFunction implements ParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        boolean withKeyword = builder.getTokenType() == BashTokenTypes.FUNCTION_KEYWORD;
        return withKeyword || ParserUtil.hasNextTokens(builder, false, BashTokenTypes.WORD, BashTokenTypes.LEFT_PAREN, BashTokenTypes.RIGHT_PAREN);

    }

    public boolean parse(BashPsiBuilder builder) {
        /*
       function_def:
           WORD '(' ')' newline_list function_body
            |  FUNCTION WORD '(' ')' newline_list function_body
            |  FUNCTION WORD newline_list function_body
            ;

            function_body:
                shell_command
            |   shell_command redirection_list
            ;
         */

        final PsiBuilder.Marker function = builder.mark();

        final IElementType firstToken = builder.getTokenType();
        if (firstToken == BashTokenTypes.FUNCTION_KEYWORD) {
            builder.advanceLexer();//after the function keyword

            //get the function name
            final PsiBuilder.Marker nameMarker = builder.mark();

            //marks the function name as a symbol
            final IElementType nameToken = ParserUtil.getTokenAndAdvance(builder);
            if (nameToken != BashTokenTypes.WORD) {
                nameMarker.drop();
                ParserUtil.error(function, "parser.unexpected.token");
                return false;
            }

            nameMarker.done(BashElementTypes.SYMBOL_ELEMENT);

            //optional ()
            if (builder.getTokenType() == BashTokenTypes.LEFT_PAREN) {
                builder.advanceLexer();
                if (builder.getTokenType() != BashTokenTypes.RIGHT_PAREN) {
                    ParserUtil.error(function, "parser.unexpected.token");
                    return false;
                }

                builder.advanceLexer();
            }
        } else if (firstToken == BashTokenTypes.WORD) {
            //parse something like a()
            ParserUtil.markTokenAndAdvance(builder, BashElementTypes.SYMBOL_ELEMENT);

            final IElementType leftBracket = ParserUtil.getTokenAndAdvance(builder);
            if (leftBracket != BashTokenTypes.LEFT_PAREN) {
                ParserUtil.error(function, "parser.unexpected.token");
                return false;
            }

            final IElementType rightBracket = ParserUtil.getTokenAndAdvance(builder);
            if (rightBracket != BashTokenTypes.RIGHT_PAREN) {
                ParserUtil.error(function, "parser.unexpected.token");
                return false;
            }
        } else {
            ParserUtil.error(function, "parser.unexpected.token");
            return false;
        }

        //optional newlines before the body
        final boolean newlinesAtBegin = builder.eatOptionalNewlines();

        //if we don't have one or more newlines we need a command group, i.e. {...}
        boolean isGroup = Parsing.shellCommand.groupCommandParser.isValid(builder);
        if (!newlinesAtBegin && !isGroup) {
            function.drop();

            ParserUtil.errorToken(builder, "parser.unexpected.token");
            return false;
        }

        //parse function body
        final PsiBuilder.Marker body = builder.mark();
        boolean parsed = Parsing.shellCommand.parse(builder);
        if (!parsed) {
            body.drop();
            function.drop();
            return false;
        }

        if (!isGroup && builder.getTokenType() == BashTokenTypes.SEMI) {
            builder.advanceLexer();
        }

        body.done(BashElementTypes.BLOCK_ELEMENT);

        function.done(BashElementTypes.FUNCTION_DEF_COMMAND);
        return true;
    }
}
