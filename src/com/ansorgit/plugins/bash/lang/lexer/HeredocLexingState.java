package com.ansorgit.plugins.bash.lang.lexer;

import com.google.common.collect.Lists;

import java.util.LinkedList;

/**
 * Herredoc lexing state used in the lexer
 */
class HeredocLexingState {
    private LinkedList<String> expectedHeredocs = Lists.newLinkedList();

    public boolean isNextHeredocMarker(String marker) {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().equals(cleanMarker(marker));
    }

    public void pushHeredocMarker(String marker) {
        expectedHeredocs.add(cleanMarker(marker));
    }

    public void popHeredocMarker(String marker) {
        if (!isNextHeredocMarker(cleanMarker(marker))) {
            throw new IllegalStateException("Heredoc marker isn't expected to be removed: " + marker);
        }

        expectedHeredocs.removeFirst();
    }

    public boolean isEmpty() {
        return expectedHeredocs.isEmpty();
    }

    private String cleanMarker(String marker) {
        int start = 0;
        int end = marker.trim().length();

        if (marker.charAt(0) == '$') {
            start++;
        }

        if (marker.charAt(start) == '\'' || marker.charAt(start) == '"') {
            start++;
            end--;
        }

        //fixme handle concatenated ''"" parts with optional $ prefix chars
        return marker.substring(start, end);
    }
}
