/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ConditionalParsingFunction.java, Class: ConditionalParsingFunction
 * Last modified: 2010-02-09
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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing of conditional statements, like [ -z "" ] .
 * Date: 02.05.2009
 * Time: 11:22:03
 *
 * @author Joachim Ansorg
 */
public class ConditionalParsingFunction extends DefaultParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ConditionalParsingFunction");

    public boolean isValid(BashPsiBuilder builder) {
        IElementType token = builder.getTokenType();
        return token == EXPR_CONDITIONAL || token == BRACKET_KEYWORD;
    }

    /**
     * Parses the next tokens as a conditional command.
     *
     * @param builder Provides the tokens.
     * @return Success or failure of the parsing.
     */
    public boolean parse(final BashPsiBuilder builder) {
        log.assertTrue(builder.getTokenType() == EXPR_CONDITIONAL || builder.getTokenType() == BRACKET_KEYWORD);

        final PsiBuilder.Marker command = builder.mark();
        try {
            return parseConditionalExpression(builder);
        } finally {
            command.done(CONDITIONAL_COMMAND);
        }
    }

    /**
     * Parses a conidional expression .
     *
     * @param builder Provides the tokens
     * @return Success or failure.
     */
    private boolean parseConditionalExpression(BashPsiBuilder builder) {
        final IElementType firstToken = ParserUtil.getTokenAndAdvance(builder);
        final boolean simpleMode = firstToken == EXPR_CONDITIONAL;

        //fixme
        boolean success = true;
        IElementType tokenType = builder.getTokenType();
        while (!isEndToken(tokenType) && success) {
            if (conditionalOperators.contains(tokenType) || ParserUtil.isWordToken(tokenType)) {
                builder.advanceLexer();
            } else if (Parsing.word.isWordToken(builder, true)) {
                success = Parsing.word.parseWord(builder, true);//fixme set reject?
            } else {
                success = false;
            }

            tokenType = builder.getTokenType();
        }

        //read trailing whitespace, might occur before the closing expression

        if (simpleMode && builder.getTokenType() == _EXPR_CONDITIONAL) {
            builder.advanceLexer();
            return true;
        } else if (!simpleMode && builder.getTokenType() == _BRACKET_KEYWORD) {
            builder.advanceLexer();
            return true;
        } else {
            ParserUtil.error(builder, "parser.shell.conditional.expectedEnd");
            return false;
        }
    }

    private boolean isEndToken(IElementType tokenType) {
        return tokenType == _EXPR_CONDITIONAL || tokenType == _BRACKET_KEYWORD;
    }
}
