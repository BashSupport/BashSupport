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

package com.ansorgit.plugins.bash.lang.parser;

import java.util.HashSet;
import java.util.Set;

/**
 * Data container to track the advanced parsing state.
 * It can track whether the parser currently is in a heredoc or a simple command.
 * <br>
 *
 * @author jansorg
 */
public final class ParsingStateData {
    //do we have to use the volatile? Currently it's not clear whether a PsiBuilder is called concurrently or not
    private int inSimpleCommand = 0;
    private int heredocMarkers = 0;
    private final Set<Integer> heredocMarkersIndexSet = new HashSet<>();

    public void enterSimpleCommand() {
        inSimpleCommand += 1;
    }

    public void leaveSimpleCommand() {
        inSimpleCommand -= 1;
    }

    public boolean isInSimpleCommand() {
        return inSimpleCommand > 0;
    }

    public void pushHeredocMarker(int id) {
        if (!heredocMarkersIndexSet.contains(id)) {
            heredocMarkers++;
            heredocMarkersIndexSet.add(id);
        }
    }

    public boolean expectsHeredocMarker() {
        return heredocMarkers > 0;
    }

    public void popHeredocMarker() {
        heredocMarkers--;
        if (heredocMarkers <= 0) {
            heredocMarkersIndexSet.clear();
        }
    }

    public Set<Integer> getHeredocMarkersIndexSet() {
        return heredocMarkersIndexSet;
    }
}
