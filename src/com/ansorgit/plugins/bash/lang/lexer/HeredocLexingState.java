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

package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.lang.util.HeredocSharedImpl;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * Heredoc lexing state used in the lexer
 */
final class HeredocLexingState {
    private final LinkedList<HeredocMarkerInfo> expectedHeredocs = Lists.newLinkedList();

    public boolean isEmpty() {
        return expectedHeredocs.isEmpty();
    }

    boolean isNextMarker(CharSequence markerText) {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().nameEquals(markerText);
    }

    boolean isExpectingEvaluatingHeredoc() {
        if (isEmpty()) {
            throw new IllegalStateException("isExpectingEvaluatingHeredoc called on an empty marker stack");
        }

        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().evaluating;
    }

    boolean isIgnoringTabs() {
        if (isEmpty()) {
            throw new IllegalStateException("isIgnoringTabs called on an empty marker stack");
        }

        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().ignoreLeadingTabs;
    }

    void removeMarker(long offset) {
        // remove existing markers at the same offset
        expectedHeredocs.removeIf(info -> info.offset == offset);
    }

    void pushMarker(long offset, CharSequence marker, boolean ignoreTabs) {
        if (offset >= 0) {
            // if there already is a marker at the same offset, then we're overriding it instead of adding a new marker
            removeMarker(offset);
        }
        expectedHeredocs.add(new HeredocMarkerInfo(offset, marker, ignoreTabs));
    }

    void popMarker(CharSequence marker) {
        if (!isNextMarker(HeredocSharedImpl.cleanMarker(marker.toString(), false))) {
            throw new IllegalStateException("Heredoc marker isn't expected to be removed: " + marker);
        }

        expectedHeredocs.removeFirst();
    }

    private static class HeredocMarkerInfo {
        final boolean ignoreLeadingTabs;
        final boolean evaluating;
        final CharSequence markerName;
        private final long offset;

        HeredocMarkerInfo(long offset, CharSequence markerText, boolean ignoreLeadingTabs) {
            String markerTextString = markerText.toString();

            this.offset = offset;
            this.markerName = HeredocSharedImpl.cleanMarker(markerTextString, ignoreLeadingTabs);
            this.evaluating = HeredocSharedImpl.isEvaluatingMarker(markerTextString);
            this.ignoreLeadingTabs = ignoreLeadingTabs;
        }

        boolean nameEquals(CharSequence markerText) {
            return this.markerName.equals(HeredocSharedImpl.cleanMarker(markerText.toString(), ignoreLeadingTabs));
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            HeredocMarkerInfo that = (HeredocMarkerInfo) o;

            if (ignoreLeadingTabs != that.ignoreLeadingTabs) {
                return false;
            }
            if (evaluating != that.evaluating) {
                return false;
            }
            return markerName != null ? markerName.equals(that.markerName) : that.markerName == null;

        }

        @Override
        public int hashCode() {
            int result = (ignoreLeadingTabs ? 1 : 0);
            result = 31 * result + (evaluating ? 1 : 0);
            result = 31 * result + (markerName != null ? markerName.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "HeredocMarkerInfo{" +
                    "offset=" + offset +
                    ", markerName=" + markerName +
                    '}';
        }
    }
}
