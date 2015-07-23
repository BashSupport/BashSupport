package com.ansorgit.plugins.bash.lang.util;

import org.apache.commons.lang.StringUtils;

/**
 * Shared code for the Heredoc handling.
 */
public class HeredocSharedImpl {
    public static String cleanMarker(String marker) {
        String markerText = trimNewline(marker);
        if (markerText.equals("$")) {
            return markerText;
        }

        int start = startMarkerTextOffset(markerText);
        int end = endMarkerTextOffset(markerText);

        return end < markerText.length() && start < end ? markerText.substring(start, end) : marker;
    }

    public static int startMarkerTextOffset(String markerText) {
        int start = 0;
        int length = markerText.length();

        if (markerText.charAt(start) == '\\') {
            return start + 1;
        }

        if (markerText.charAt(start) == '$') {
            start++;
        }

        if (start < length && markerText.charAt(start) == '\\') {
            start++;
        }

        if (start < length && (markerText.charAt(start) == '\'' || markerText.charAt(start) == '"')) {
            start++;
        }

        return start;
    }

    public static int endMarkerTextOffset(String markerText) {
        int end = markerText.length() - 1;

        while (end > 0  && markerText.charAt(end) == '\n') {
            end--;
        }

        if (end > 0 && markerText.charAt(end) == '\'' || markerText.charAt(end) == '"') {
            end--;
        }

        return end + 1;
    }

    public static boolean isEvaluatingMarker(String marker) {
        String markerText = trimNewline(marker);

        return !markerText.startsWith("\"") && !markerText.startsWith("'") && !markerText.startsWith("\\") && !markerText.startsWith("$");
    }

    private static String trimNewline(String marker) {
        return StringUtils.removeEnd(marker, "\n");
    }

    public static String wrapMarker(String newName, String originalMarker) {
        int start = startMarkerTextOffset(originalMarker);
        int end = endMarkerTextOffset(originalMarker);

        return (end <= originalMarker.length() && start < end)
                ? originalMarker.substring(0, start) + newName + originalMarker.substring(end)
                : newName;
    }
}
