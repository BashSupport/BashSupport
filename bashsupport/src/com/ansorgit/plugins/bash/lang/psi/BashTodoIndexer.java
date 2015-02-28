package com.ansorgit.plugins.bash.lang.psi;

import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;

public class BashTodoIndexer extends LexerBasedTodoIndexer {
    @Override
    public Lexer createLexer(OccurrenceConsumer consumer) {
        return BashIdIndexer.createIndexingLexer(consumer);
    }
}
