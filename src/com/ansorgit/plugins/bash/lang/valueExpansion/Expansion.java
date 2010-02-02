/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: Expansion.java, Class: Expansion
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
 * An expansion is a single block in a combined expansion. E.g. {1..3}a is a combined expansion
 * which consists of one iterating expansion {1..3} and one static expansion (a).
 * <p/>
 * User: jansorg
 * Date: Nov 11, 2009
 * Time: 8:41:57 PM
 */
interface Expansion {
    /**
     * Goes to the next value and returns the new state.
     *
     * @return The new state.
     */
    public String findNext(boolean previousFlipped);

    /**
     * Returns if the last findNext call flipped this expansion, i.e. if this expansion is
     * currently at the first value again after a full iteration of values.
     *
     * @return True if this expansion is currently back at the first element.
     */
    public boolean isFlipped();

    /**
     * Returns whether there at least one more element in the current iteration of the available value.
     *
     * @return True if there one more value or false if the last returned value was the last one of the expansion.
     */
    public boolean hasNext();
}
