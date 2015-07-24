package com.ansorgit.plugins.bash.lang.util;

import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

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

        return end <= markerText.length() && start < end ? markerText.substring(start, end) : marker;
    }

    public static int startMarkerTextOffset(String markerText) {
        return getStartEndOffsets(markerText).first;
    }

    public static int endMarkerTextOffset(String markerText) {
        return getStartEndOffsets(markerText).second;
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

    public static Pair<Integer, Integer> getStartEndOffsets(@NotNull String markerText) {
        if (markerText.isEmpty()) {
            return Pair.create(0, 0);
        }

        if (markerText.length() == 1) {
            return Pair.create(0, 1);
        }

        if (markerText.charAt(0) == '\\' && markerText.length() > 1) {
            return Pair.create(1, markerText.length());
        }

        int length = markerText.length();
        int start = 0;
        int end = length - 1;

        if (markerText.charAt(start) == '$' && length > 2 && (markerText.charAt(start + 1) == '"' || markerText.charAt(end) == '\'')) {
            start++;
            length--;
        }

        while (end > 0 && markerText.charAt(end) == '\n') {
            end--;
        }

        if (length > 0 && (markerText.charAt(start) == '\'' || markerText.charAt(start) == '"') && markerText.charAt(end) == markerText.charAt(start)) {
            start++;
            end--;
            length -= 2;
        }

        return Pair.create(start, end + 1);
    }
}
