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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import org.junit.Test;

public class BashSyntaxHighlighterTest extends AbstractBashSyntaxHighlighterTest {
    @Override
    protected String getBasePath() {
        return "editor/highlighting/syntaxHighlighter";
    }

    @Test
    public void testBackquote() {
        doHighlightingTest();
    }

    @Test
    public void testHeredoc() {
        doHighlightingTest();
    }

    @Test
    public void testRedirect() {
        doHighlightingTest();
    }

    @Test
    public void testSubshell() {
        doHighlightingTest();
    }

    @Test
    public void testSimpleVariable() {
        doHighlightingTest();
    }

    @Test
    public void testBuiltinVariable() {
        doHighlightingTest();
    }

    @Test
    public void testBuiltinCommand() {
        doHighlightingTest();
    }

    @Test
    public void testFunctionDef() {
        doHighlightingTest();
    }

    @Test
    public void _testLexerHighlighting() {
        //keywords
        doLexerHighlightingTest("for", BashTokenTypes.FOR_KEYWORD);
        doLexerHighlightingTest("while", BashTokenTypes.WHILE_KEYWORD);
        doLexerHighlightingTest("!", BashTokenTypes.BANG_TOKEN);
        doLexerHighlightingTest("then", BashTokenTypes.THEN_KEYWORD);
        doLexerHighlightingTest("$", BashTokenTypes.DOLLAR);
        doLexerHighlightingTest("done", BashTokenTypes.DONE_KEYWORD);

        //internal commands
        doLexerHighlightingTest("trap", BashTokenTypes.TRAP_KEYWORD);
        doLexerHighlightingTest("let", BashTokenTypes.LET_KEYWORD);

        doLexerHighlightingTest("$VAR", BashTokenTypes.VARIABLE);

        doLexerHighlightingTest("#X", BashTokenTypes.COMMENT);

        doLexerHighlightingTest("#!/bin/bash", BashTokenTypes.SHEBANG);

        doLexerHighlightingTest("'string'", BashTokenTypes.STRING2);

        doLexerHighlightingTest("(test)", BashTokenTypes.LEFT_PAREN);
        doLexerHighlightingTest("(test)", BashTokenTypes.RIGHT_PAREN);

        doLexerHighlightingTest("${x}", BashTokenTypes.LEFT_CURLY);
        doLexerHighlightingTest("${x}", BashTokenTypes.RIGHT_CURLY);

        doLexerHighlightingTest("a > x", BashTokenTypes.GREATER_THAN);
        doLexerHighlightingTest("a < x", BashTokenTypes.LESS_THAN);
        doLexerHighlightingTest("a &> x", BashTokenTypes.REDIRECT_AMP_GREATER);
        doLexerHighlightingTest("a >> x", BashTokenTypes.SHIFT_RIGHT);

        doLexerHighlightingTest("a && b", BashTokenTypes.AND_AND);
        doLexerHighlightingTest("a || b", BashTokenTypes.OR_OR);
    }

}