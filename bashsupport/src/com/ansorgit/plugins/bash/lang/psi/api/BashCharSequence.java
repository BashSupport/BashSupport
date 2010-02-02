/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashCharSequence.java, Class: BashCharSequence
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.psi.api;

/**
 * User: jansorg
 * Date: Nov 2, 2009
 * Time: 8:46:06 PM
 */
public interface BashCharSequence extends BashPsiElement {
    /**
     * Returns the char sequence without any start or end markers.
     *
     * @return The string value without marks
     */
    public String getUnwrappedCharSequence();

    /**
     * Returns whether this char sequence is static or whether its value depends on runtime information.
     * A static sequence does not contain any variables, etc.
     *
     * @return True if its value is known at write-time.
     */
    public boolean isStatic();
}
