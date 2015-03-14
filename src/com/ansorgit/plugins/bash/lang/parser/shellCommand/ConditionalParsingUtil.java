package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.intellij.psi.tree.TokenSet;

/**
 * User: jansorg
 * Date: 09.11.11
 * Time: 21:20
 */
public class ConditionalParsingUtil {
    private static TokenSet operators = TokenSet.create(BashTokenTypes.COND_OP, BashTokenTypes.COND_OP_EQ_EQ, BashTokenTypes.COND_OP_REGEX);
    private static TokenSet regExpEndTokens = TokenSet.create(BashTokenTypes.WHITESPACE, BashTokenTypes._BRACKET_KEYWORD);

    private ConditionalParsingUtil() {
    }

    public static boolean readTestExpression(BashPsiBuilder builder, TokenSet endTokens) {
        //fixme implement more intelligent test expression parsing

        boolean ok = true;

        while (ok && !endTokens.contains(builder.getTokenType())) {
            if (Parsing.word.isWordToken(builder)) {
                ok = Parsing.word.parseWord(builder);
            } else if (builder.getTokenType() == BashTokenTypes.COND_OP_NOT) {
                builder.advanceLexer();
                ok = readTestExpression(builder, endTokens);
            } else if (builder.getTokenType() == BashTokenTypes.COND_OP_REGEX) {
                builder.advanceLexer();

                //eat optional whitespace in front
                if (builder.getTokenType(true) == BashTokenTypes.WHITESPACE) {
                    //builder.advanceLexer();
                }

                //parse the regex
                ok = parseRegularExpression(builder);
            } else if (operators.contains(builder.getTokenType())) {
                builder.advanceLexer();
            } else {
                ok = false;
                break;
            }
        }

        return ok;
    }

    public static boolean parseRegularExpression(BashPsiBuilder builder) {
        int count = 0;

        //simple solution: read to the next whitespace, unless we are in [] brackets
        while (!builder.eof() && !regExpEndTokens.contains(builder.rawLookup(0))) {
            builder.advanceLexer();
            count++;
        }

        return count > 0;
    }
}
