/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: IteratingExpansion.java, Class: IteratingExpansion
 * Last modified: 2010-06-30
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * A single expansion block with more than one value.
 * <p/>
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 8:47:00 PM
 */
class IteratingExpansion implements Expansion {
    private final List<String> values = new LinkedList<String>();
    private Iterator<String> valueIterator;
    private String currentValue;
    private int index = 0;
    private int count = 0;
    private boolean isFlipped = false;

    public IteratingExpansion(List<String> values) {
        this.values.addAll(values);
        this.valueIterator = this.values.iterator();
        this.currentValue = valueIterator.hasNext() ? valueIterator.next() : null;
    }

    /**
     * Find the next value of the value chain.
     *
     * @param previousFlipped True if the previous element just flipped.
     * @return The new vlaue.
     */
    public String findNext(boolean previousFlipped) {
        boolean resetIsFlipped = true;

        if (previousFlipped && count > 0) {
            if (!valueIterator.hasNext()) {
                valueIterator = values.iterator();
                index = 0;
                isFlipped = true;
                resetIsFlipped = false;
            } else {
                index++;
            }

            currentValue = valueIterator.next();
        }

        count++;

        if (resetIsFlipped) {
            isFlipped = false;
        }

        return currentValue;
    }

    public boolean isFlipped() {
        return index == 0 && isFlipped;
    }

    public boolean hasNext() {
        return valueIterator.hasNext();
    }
}
