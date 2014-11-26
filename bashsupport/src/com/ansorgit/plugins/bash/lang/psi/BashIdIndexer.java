package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.ansorgit.plugins.bash.lang.psi.stubs.index.BashIndexVersion;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.id.LexerBasedIdIndexer;

public class BashIdIndexer extends LexerBasedIdIndexer {
    public static BashFilterLexer createIndexingLexer(OccurrenceConsumer occurendeConsumer) {
        return new BashFilterLexer(new BashLexer(), occurendeConsumer);
    }

    @Override
    public Lexer createLexer(OccurrenceConsumer consumer) {
        return createIndexingLexer(consumer);
    }

    @Override
    public int getVersion() {
        return BashIndexVersion.ID_VERSION;
    }
}
