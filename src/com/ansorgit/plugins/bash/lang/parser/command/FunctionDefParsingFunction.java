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
 * <br>
 * @author jansorg
 */
public class FunctionDefParsingFunction implements ParsingFunction {
    // tokens which start a function definition
    private static final IElementType[] FUNCTION_DEF_TOKENLIST = {BashTokenTypes.WORD, BashTokenTypes.LEFT_PAREN, BashTokenTypes.RIGHT_PAREN};

    public boolean isValid(BashPsiBuilder builder) {
        IElementType current = builder.getTokenType();
        return current == BashTokenTypes.FUNCTION_KEYWORD
                || current == WORD && ParserUtil.hasNextTokens(builder, false, FUNCTION_DEF_TOKENLIST);
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

            nameMarker.done(BashElementTypes.FUNCTION_DEF_NAME_ELEMENT);

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
            ParserUtil.markTokenAndAdvance(builder, BashElementTypes.FUNCTION_DEF_NAME_ELEMENT);

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
        builder.readOptionalNewlines();

        //parse function body
        PsiBuilder.Marker bodyMarker = builder.mark();

        boolean parsed = Parsing.shellCommand.parse(builder);
        if (!parsed) {
            //mark the definition header (i.e. the function name) as function definition, so resolving works as expected
            function.doneBefore(BashElementTypes.FUNCTION_DEF_COMMAND, bodyMarker);
            bodyMarker.drop();

            ParserUtil.errorToken(builder, "parser.unexpected.token");

            return true;
        }

        //needed only to mark the function name with parsing errors
        bodyMarker.drop();

        //optional semicolon, not sure if this is compatible with the spec
        if (builder.getTokenType() == BashTokenTypes.SEMI) {
            builder.advanceLexer();
        }

        function.done(BashElementTypes.FUNCTION_DEF_COMMAND);

        return true;
    }
}
