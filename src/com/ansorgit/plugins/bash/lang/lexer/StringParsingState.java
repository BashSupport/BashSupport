/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: StringParsingState.java, Class: StringParsingState
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.lang.lexer;

import java.util.Stack;

/**
 * Class to store information about the current parsing of strings.
 * In bash strings can be nested. An expression like "$("$("abcd")")" is one string which contains subshell commands.
 * Each subshell command contains a separate string.  To parse this we need a stack of parsing states.
 * This is what this class does.
 * <p/>
 * Date: 16.04.2009
 * Time: 23:21:08
 *
 * @author Joachim Ansorg
 */
public final class StringParsingState {
    private final StringBuilder stringData = new StringBuilder(256);
    //private final static Logger log = Logger.getInstance("#bash.StringParsingState");

    private static final class SubshellState {
        private boolean inString = false;
        private int openParenths = 0;
        private boolean freshStart = true;

        public boolean isInString() {
            return inString;
        }

        public void enterSubstring() {
            assert !inString;
            inString = true;
        }

        public void leaveSubstring() {
            assert inString;
            inString = false;
        }

        public void enterParenth() {
            //assert !inString;
            assert openParenths >= 0;
            openParenths++;
        }

        public void leaveParenth() {
            assert openParenths > 0;
            openParenths--;
        }

        public void advanceToken() {
            freshStart = false;
        }
    }

    private final Stack<SubshellState> subshells = new Stack<SubshellState>();

    public void reset() {
        stringData.setLength(0);
        subshells.removeAllElements();
    }

    public boolean isInSubstring() {
        return !subshells.isEmpty() && subshells.peek().isInString();
    }

    public boolean isNewAllowed() {
        return isInSubshell() && !isInSubstring();
    }

    public void enterSubstring() {
        if (!isNewAllowed()) {
            throw new IllegalStateException("New string is not alllowed");
        }

        subshells.peek().enterSubstring();
    }

    public void leaveSubstring() {
        if (!isInSubshell()) {
            throw new IllegalStateException("not in subshell");
        }
        if (!isInSubstring()) {
            throw new IllegalStateException("not in string");
        }

        subshells.peek().leaveSubstring();
    }

    public boolean isInSubshell() {
        return !subshells.isEmpty();
    }

    public void enterSubshell() {
        subshells.push(new SubshellState());
    }

    public void leaveSubshell() {
        assert !subshells.isEmpty();

        if (!subshells.isEmpty()) {
            if (subshells.peek().openParenths > 0) {
                subshells.peek().leaveParenth();
            } else {
                subshells.pop();
            }
        }
    }

    public void enterSubshellParenth() {
        assert !subshells.isEmpty();
        if (!subshells.peek().freshStart) {
            subshells.peek().enterParenth();
        }
    }

    public void advanceToken() {
        if (!subshells.isEmpty()) {
            subshells.peek().advanceToken();
        }
    }

    public boolean isFreshSubshell() {
        return !subshells.isEmpty() && subshells.peek().freshStart;
    }
}
