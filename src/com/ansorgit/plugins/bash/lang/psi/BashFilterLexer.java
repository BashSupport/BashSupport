package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.BaseFilterLexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.tree.IElementType;

public class BashFilterLexer extends BaseFilterLexer {

    protected BashFilterLexer(Lexer lexer, OccurrenceConsumer consumer) {
        super(lexer, consumer);
    }

    @Override
    public void advance() {
        IElementType tokenType = myDelegate.getTokenType();

        if (tokenType == BashTokenTypes.COMMENT) {
            scanWordsInToken(UsageSearchContext.IN_COMMENTS, false, false);
            advanceTodoItemCountsInToken();
        } else if (tokenType == BashTokenTypes.WORD) {
            addOccurrenceInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS);
            scanWordsInToken(UsageSearchContext.IN_CODE | UsageSearchContext.IN_STRINGS, false, false);
        } else if (tokenType == BashTokenTypes.STRING2) {
            scanWordsInToken(UsageSearchContext.IN_STRINGS, true, false);
        }

        myDelegate.advance();
    }
}
