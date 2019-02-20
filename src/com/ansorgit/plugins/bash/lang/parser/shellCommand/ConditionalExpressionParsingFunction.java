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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing of conditional statements, like [ -z "" ] .
 * <br>
 * @author jansorg
 */
public class ConditionalExpressionParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ConditionalParsingFunction");

    private static final TokenSet conditionalRejects = TokenSet.create(_EXPR_CONDITIONAL);

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == EXPR_CONDITIONAL;
    }

    /**
     * Parses the next tokens as a conditional command.
     *
     * @param builder Provides the tokens.
     * @return Success or failure of the parsing.
     */
    public boolean parse(final BashPsiBuilder builder) {
        final PsiBuilder.Marker command = builder.mark();

        boolean result = parseConditionalExpression(builder);

        command.done(CONDITIONAL_COMMAND);
        return result;
    }

    /**
     * Parses a conidional expression, this may either be a test expression or a conditional command.
     *
     * @param builder Provides the tokens
     * @return Success or failure.
     */
    private boolean parseConditionalExpression(BashPsiBuilder builder) {
        ParserUtil.getTokenAndAdvance(builder);

        boolean success = true;

        IElementType tokenType = builder.getTokenType();
        while (!isEndToken(tokenType) && success) {
            if (ParserUtil.isWordToken(tokenType)) {
                builder.advanceLexer();
            } else {
                OptionalParseResult result = Parsing.word.parseWordIfValid(builder, true, conditionalRejects, TokenSet.EMPTY, null);
                if (result.isValid()) {
                    success = result.isParsedSuccessfully();
                } else {
                    success = ConditionalParsingUtil.readTestExpression(builder, conditionalRejects);
                }
            }

            tokenType = builder.getTokenType();
        }

        //read trailing whitespace, might occur before the closing expression

        if (builder.getTokenType() == _EXPR_CONDITIONAL) {
            builder.advanceLexer();
            return true;
        }

        ParserUtil.error(builder, "parser.shell.conditional.expectedEnd");
        return false;
    }

    private boolean isEndToken(IElementType tokenType) {
        return tokenType == _EXPR_CONDITIONAL;
    }
}
