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
 * <p/>
 * User: jansorg
 * Date: 09.11.11
 * Time: 20:06
 */
public class ConditionalCommandParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ConditionalCommandParsingFunction");

    private TokenSet endTokens = TokenSet.create(_BRACKET_KEYWORD, AND_AND, OR_OR, RIGHT_PAREN);

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == BashTokenTypes.BRACKET_KEYWORD;
    }

    /**
     * From http://www.gnu.org/software/bash/manual/bashref.html#Conditional-Constructs :
     * <p/>
     * <p/>
     * <code>( expression )</code>
     * Returns the value of expression. This may be used to override the normal precedence of operators.
     * <p/>
     * <code>! expression</code>
     * True if expression is false.
     * <p/>
     * <code>expression1 && expression2</code>
     * True if both expression1 and expression2 are true.
     * <p/>
     * <code>expression1 || expression2</code>
     * True if either expression1 or expression2 is true.
     * <p/>
     * The && and || operators do not evaluate expression2 if the value of expression1 is sufficient to determine the return value of the entire conditional expression.
     * <p/>
     * An expression is a normal test expression as used in the conditional expression parsing function.
     *
     * @param builder The provider of the tokens.
     * @return True if the parsing was successful
     */
    @Override
    public boolean parse(BashPsiBuilder builder) {
        IElementType token = builder.getTokenType(false);
        log.assertTrue(token == BRACKET_KEYWORD);

        PsiBuilder.Marker startMarker = builder.mark();
        builder.advanceLexer();

        boolean ok;
        if (builder.getTokenType(false) == _BRACKET_KEYWORD) {
            builder.error("Empty expression is not allowed");
            ok = false;
        } else {
            ok = parseExpression(builder);
        }

        ok &= builder.getTokenType() == _BRACKET_KEYWORD;

        if (ok) {
            builder.advanceLexer();
            startMarker.done(BashElementTypes.CONDITIONAL_COMMAND);
            return true;
        }

        startMarker.drop();
        return false;
    }

    private boolean parseExpression(BashPsiBuilder builder) {
        boolean ok = true;

        int counter = 0;

        //PsiBuilder.Marker marker = builder.mark();

        while (ok) {
            IElementType token = builder.getTokenType();

            //bracket subexpression, e.g. (-f x.txt)
            if (token == LEFT_PAREN) {
                builder.advanceLexer();
                ok = parseExpression(builder);
                ok &= ParserUtil.conditionalRead(builder, RIGHT_PAREN);
            } else if (token == BANG_TOKEN) {
                builder.advanceLexer();
                ok = parseExpression(builder);
            } else if (counter >= 1 && token == OR_OR) {
                builder.advanceLexer();
                ok = parseExpression(builder);
            } else if (counter >= 1 && token == AND_AND) {
                builder.advanceLexer();
                ok = parseExpression(builder);
            } else {
                ok = ConditionalParsingUtil.readTestExpression(builder, endTokens);
            }

            if (ok) {
                counter++;
            }

            if (RIGHT_PAREN == builder.getTokenType() || _BRACKET_KEYWORD == builder.getTokenType()) {
                break;
            }
        }

        /*if (ok) {
            marker.done(CONDITIONAL_EXPRESSION);
        } else {
            marker.drop();
        } */

        return ok;
    }

}
