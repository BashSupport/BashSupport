package com.ansorgit.plugins.bash.lang.lexer;

import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedList;

/**
 * Heredoc lexing state used in the lexer
 */
class HeredocLexingState {
    private LinkedList<Pair<String, Boolean>> expectedHeredocs = Lists.newLinkedList();

    public boolean isNextHeredocMarker(String marker) {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().first.equals(trimNewline(marker));
    }

    private String trimNewline(String marker) {
        return StringUtils.removeEnd(marker, "\n");
    }

    public void pushHeredocMarker(String marker) {
        expectedHeredocs.add(Pair.create(cleanMarker(marker), isEvaluatingMarker(marker)));
    }

    public boolean isExpectingEvaluatingHeredoc() {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().second;
    }

    private static Boolean isEvaluatingMarker(String marker) {
        return !marker.startsWith("\"") && !marker.startsWith("'") && !marker.startsWith("\\") && !marker.startsWith("$");
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
        if (marker.equals("$")) {
            return marker;
        }

        int start = 0;
        int end = marker.length();

        if (marker.charAt(start) == '$') {
            start++;
        }

        if (start < end && marker.charAt(start) == '\\') {
            start++;
        }

        while (end > 0 && marker.charAt(end - 1) == '\n') {
            end--;
        }

        if (start < end && end > 0 && marker.charAt(start) == '\'' || marker.charAt(start) == '"') {
            start++;
            end--;
        }

        //fixme handle concatenated ''"" parts with optional $ prefix chars
        return marker.substring(start, end);
    }
}
