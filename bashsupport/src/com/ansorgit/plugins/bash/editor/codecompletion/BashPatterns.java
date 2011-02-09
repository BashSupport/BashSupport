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
