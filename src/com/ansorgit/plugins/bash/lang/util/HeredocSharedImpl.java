/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.util;

import com.intellij.openapi.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * Shared code for the Heredoc handling.
 */
public final class HeredocSharedImpl {
    private HeredocSharedImpl() {
    }

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

    public static String wrapMarker(String newName, String originalMarker) {
        int start = startMarkerTextOffset(originalMarker);
        int end = endMarkerTextOffset(originalMarker);

        return (end <= originalMarker.length() && start < end)
                ? originalMarker.substring(0, start) + newName + originalMarker.substring(end)
                : newName;
    }

    private static String trimNewline(String marker) {
        return StringUtils.removeEnd(marker, "\n");
    }

    private static Pair<Integer, Integer> getStartEndOffsets(@NotNull String markerText) {
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
        }

        return Pair.create(start, end + 1);
    }
}
