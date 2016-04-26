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
import com.intellij.openapi.util.Pair;

import java.util.LinkedList;

/**
 * Heredoc lexing state used in the lexer
 */
class HeredocLexingState {
    private final LinkedList<Pair<String, Boolean>> expectedHeredocs = Lists.newLinkedList();

    boolean isNextHeredocMarker(String marker) {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().first.equals(HeredocSharedImpl.cleanMarker(marker));
    }

    void pushHeredocMarker(String marker) {
        expectedHeredocs.add(Pair.create(HeredocSharedImpl.cleanMarker(marker), HeredocSharedImpl.isEvaluatingMarker(marker)));
    }

    boolean isExpectingEvaluatingHeredoc() {
        return !expectedHeredocs.isEmpty() && expectedHeredocs.peekFirst().second;
    }

    void popHeredocMarker(String marker) {
        if (!isNextHeredocMarker(HeredocSharedImpl.cleanMarker(marker))) {
            throw new IllegalStateException("Heredoc marker isn't expected to be removed: " + marker);
        }

        expectedHeredocs.removeFirst();
    }

    public boolean isEmpty() {
        return expectedHeredocs.isEmpty();
    }
}
