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
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing of conditional commands like [[ -f x.txt && -d dir1 ]]
 * <br>
 *
 * @author jansorg
 */
public class ConditionalCommandParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ConditionalCommandParsingFunction");

    private static final TokenSet endTokens = TokenSet.create(_BRACKET_KEYWORD, AND_AND, OR_OR);

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == BashTokenTypes.BRACKET_KEYWORD;
    }

    /**
     * From http://www.gnu.org/software/bash/manual/bashref.html#Conditional-Constructs :
     * <br>
     * <br>
     * <code>( expression )</code>
     * Returns the value of expression. This may be used to override the normal precedence of operators.
     * <br>
     * <code>! expression</code>
     * True if expression is false.
     * <br>
     * <code>expression1 && expression2</code>
     * True if both expression1 and expression2 are true.
     * <br>
     * <code>expression1 || expression2</code>
     * True if either expression1 or expression2 is true.
     * <br>
     * The && and || operators do not evaluate expression2 if the value of expression1 is sufficient to determine the return value of the entire conditional expression.
     * <br>
     * An expression is a normal test expression as used in the conditional expression parsing function.
     *
     * @param builder The provider of the tokens.
     * @return True if the parsing was successful
     */
    @Override
    public boolean parse(BashPsiBuilder builder) {
        IElementType token = builder.getTokenType();
        log.assertTrue(token == BRACKET_KEYWORD);

        PsiBuilder.Marker startMarker = builder.mark();
        builder.advanceLexer();

        boolean ok;
        if (builder.getTokenType() == _BRACKET_KEYWORD) {
            builder.error("Empty expression is not allowed");
            ok = false;
        } else {
            ok = parseExpression(builder);
        }

        ok &= (builder.getTokenType() == _BRACKET_KEYWORD);

        if (ok) {
            builder.advanceLexer();
            startMarker.done(BashElementTypes.EXTENDED_CONDITIONAL_COMMAND);
            return true;
        }

        startMarker.drop();
        return false;
    }

    private boolean parseExpression(BashPsiBuilder builder) {
        return parseExpression(builder, TokenSet.EMPTY);
    }

    private boolean parseExpression(BashPsiBuilder builder, TokenSet additionalEndTokens) {
        boolean ok = true;

        int counter = 0;

        while (ok) {
            IElementType token = builder.getTokenType();

            //bracket subexpression, e.g. (-f x.txt)
            if (token == LEFT_PAREN) {
                builder.advanceLexer();
                ok = parseExpression(builder, TokenSet.create(RIGHT_PAREN));
                ok &= ParserUtil.conditionalRead(builder, RIGHT_PAREN);
            } else if (token == COND_OP_NOT) {
                builder.advanceLexer();
                ok = parseExpression(builder, additionalEndTokens);
            } else if (counter >= 1 && token == OR_OR) {
                builder.advanceLexer();
                ok = parseExpression(builder, additionalEndTokens);
            } else if (counter >= 1 && token == AND_AND) {
                builder.advanceLexer();
                ok = parseExpression(builder, additionalEndTokens);
            } else {
                ok = ConditionalParsingUtil.readTestExpression(builder, TokenSet.orSet(endTokens, additionalEndTokens));
            }

            if (ok) {
                counter++;
            }

            if (RIGHT_PAREN == builder.getTokenType() || _BRACKET_KEYWORD == builder.getTokenType()) {
                break;
            }
        }

        return ok;
    }
}
