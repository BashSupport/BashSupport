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

import com.intellij.util.containers.Stack;

/**
 * Class to store information about the current parsing of strings.
 * In bash strings can be nested. An expression like "$("$("abcd")")" is one string which contains subshell commands.
 * Each subshell command contains a separate string.  To parse this we need a stack of parsing states.
 * This is what this class does.
 *
 * @author jansorg
 */
final class StringLexingstate {
    private final Stack<SubshellState> subshells = new Stack<SubshellState>(5);

    void enterString() {
        if (!subshells.isEmpty()) {
            subshells.peek().enterString();
        }
    }

    void leaveString() {
        if (!subshells.isEmpty()) {
            subshells.peek().leaveString();
        }
    }

    boolean isInSubstring() {
        return !subshells.isEmpty() && subshells.peek().isInString();
    }

    boolean isSubstringAllowed() {
        return !subshells.isEmpty() && !subshells.peek().isInString();
    }

    boolean isInSubshell() {
        return !subshells.isEmpty();
    }

    void enterSubshell() {
        subshells.push(new SubshellState());
    }

    void leaveSubshell() {
        assert !subshells.isEmpty();

        subshells.pop();
    }

    private static final class SubshellState {
        private int inString = 0;

        boolean isInString() {
            return inString > 0;
        }

        void enterString() {
            inString++;
        }

        void leaveString() {
            assert inString > 0 : "The inString stack should not be empty";
            inString--;
        }
    }
}
