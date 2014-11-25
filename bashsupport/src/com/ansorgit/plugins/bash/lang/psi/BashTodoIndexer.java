package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.intellij.lexer.Lexer;
import com.intellij.psi.impl.cache.impl.OccurrenceConsumer;
import com.intellij.psi.impl.cache.impl.todo.LexerBasedTodoIndexer;

public class BashTodoIndexer extends LexerBasedTodoIndexer {
    @Override
    public Lexer createLexer(OccurrenceConsumer consumer) {
        return new BashFilterLexer(new BashLexer(BashVersion.Bash_v4), consumer);
    }
}
