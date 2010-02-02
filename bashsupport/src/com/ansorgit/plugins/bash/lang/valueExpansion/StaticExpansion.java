/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: StaticExpansion.java, Class: StaticExpansion
 * Last modified: 2010-01-27
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.lang.valueExpansion;

/**
 * A single expansion which just one element.
 * <p/>
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 8:43:53 PM
 */
class StaticExpansion implements Expansion {
    private final String value;
    private boolean lastFlipped;

    public StaticExpansion(String value) {
        this.value = value;
    }

    public String findNext(boolean previousFlipped) {
        lastFlipped = previousFlipped;
        return value;
    }

    public boolean isFlipped() {
        return lastFlipped;
    }

    public boolean hasNext() {
        return false;
    }
}
