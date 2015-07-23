package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.lang.util.HeredocSharedImpl;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;

/**
 * Heredoc lexing state used in the lexer
 */
class HeredocLexingState {
    private LinkedList<Pair<String, Boolean>> expectedHeredocs = Lists.newLinkedList();

    public boolean isNextHeredocMarker(String marker) {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().first.equals(HeredocSharedImpl.cleanMarker(marker));
    }

    public void pushHeredocMarker(String marker) {
        expectedHeredocs.add(Pair.create(HeredocSharedImpl.cleanMarker(marker), HeredocSharedImpl.isEvaluatingMarker(marker)));
    }

    public boolean isExpectingEvaluatingHeredoc() {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().second;
    }

    public void popHeredocMarker(String marker) {
        if (!isNextHeredocMarker(HeredocSharedImpl.cleanMarker(marker))) {
            throw new IllegalStateException("Heredoc marker isn't expected to be removed: " + marker);
        }

        expectedHeredocs.removeFirst();
    }

    public boolean isEmpty() {
        return expectedHeredocs.isEmpty();
    }
}
