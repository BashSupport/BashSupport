/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParsingStateData.java, Class: ParsingStateData
 * Last modified: 2010-03-09
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

package com.ansorgit.plugins.bash.lang.parser;

/**
 * User: jansorg
 * Date: Jan 29, 2010
 * Time: 7:12:36 PM
 */
final class ParsingStateData {
    private int inSimpleCommand = 0;
    private int inHereDoc = 0;
    private final Object lock = new Object();

    public void enterSimpleCommand() {
        synchronized (lock) {
            inSimpleCommand++;
        }
    }

    public void leaveSimpleCommand() {
        synchronized (lock) {
            inSimpleCommand--;
        }
    }

    public boolean isInSimpleCommand() {
        synchronized (lock) {
            return inSimpleCommand > 0;
        }
    }

    public void enterHereDoc() {
        synchronized (lock) {
            inHereDoc++;
        }
    }

    public void leaveHereDoc() {
        synchronized (lock) {
            inHereDoc--;
        }
    }

    public boolean isInHereDoc() {
        synchronized (lock) {
            return inHereDoc > 0;
        }
    }
}
