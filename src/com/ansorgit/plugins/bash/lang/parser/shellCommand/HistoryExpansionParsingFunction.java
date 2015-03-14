package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * @author Joachim Ansorg
 */
public class HistoryExpansionParsingFunction implements ParsingFunction {
    @Override
    public boolean isValid(BashPsiBuilder builder) {
        IElementType token = builder.rawLookup(1);
        return token != null && builder.getTokenType() == BANG_TOKEN && !ParserUtil.isWhitespace(token);
    }

    private final TokenSet accepted = TokenSet.create(WORD, WORD, NUMBER, BANG_TOKEN, DOLLAR);

    @Override
    public boolean parse(BashPsiBuilder builder) {
        //eat the ! token
        builder.advanceLexer();

        //following numbers specifiy the history entry
        //negative numbers count backwards
        //!! is an alias for !-1 which means the most recnt command in the history

        boolean ok = false;

        if (accepted.contains(builder.getTokenType())) {
            builder.advanceLexer();
            ok = true;
        } else if (Parsing.word.isComposedString(builder.getTokenType())) {
            ok = Parsing.word.parseComposedString(builder);
        } else {
            int count = 0;
            //fallback is here to eat up all token up to the first whitespace
            while (!builder.eof() && !ParserUtil.isWhitespace(builder.getTokenType(true))) {
                builder.advanceLexer();
                count++;
            }

            ok = count > 0;
        }

        return ok;
    }
}
