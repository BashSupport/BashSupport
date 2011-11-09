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
    private ConditionalParsingUtil() {
    }

    public static boolean readTestExpression(BashPsiBuilder builder, TokenSet endTokens) {
        //fixme implement more intelligent test expression parsing

        boolean ok = true;

        while (ok && !endTokens.contains(builder.getTokenType())) {
            if (Parsing.word.isWordToken(builder)) {
                ok = Parsing.word.parseWord(builder);
            } else if (builder.getTokenType() == BashTokenTypes.COND_OP) {
                builder.advanceLexer();
            } else {
                break;
            }
        }

        return ok;
    }

}
