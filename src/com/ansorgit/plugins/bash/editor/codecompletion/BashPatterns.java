/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPatterns.java, Class: BashPatterns
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.codecompletion;

import com.intellij.patterns.CharPattern;
import com.intellij.patterns.PatternCondition;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

/**
 * User: jansorg
 * Date: 08.02.11
 * Time: 19:10
 */
class BashPatterns {
    private BashPatterns() {
    }

    public static BashPsiPattern afterDollar = new BashPsiPattern().withText("$");

    static class BashCharPattern extends CharPattern {
        private BashCharPattern() {
        }

        CharPattern dollarChar() {
            return with(new PatternCondition<Character>("dollar") {
                public boolean accepts(@NotNull final Character character, final ProcessingContext context) {
                    return character.equals('$');
                }
            });
        }
    }

    public static BashCharPattern charPattern() {
        return new BashCharPattern();
    }
}
