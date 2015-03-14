/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: LocalCommandTest.java, Class: LocalCommandTest
 * Last modified: 2010-04-20
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * User: jansorg
 * Date: Jan 28, 2010
 * Time: 7:08:16 PM
 */
public class LocalCommandTest extends MockPsiTest {
    private MockFunction localParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return (new LocalCommand()).parse(psi);
        }
    };

    private MockFunction fileParser = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.file.parseFile(psi);
        }
    };

    @Test
    public void testBuiltin() {
        LanguageBuiltins.varDefCommands.contains("local");
    }

    @Test
    public void testParsing() {
        //   local local1=1
        mockTest(fileParser, Lists.newArrayList("local", " ", "local1"),
                INTERNAL_COMMAND, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);
    }

    @Test
    public void testParsingEmbedded() {
        //function a() {
        //   local local1=1
        //}
        mockTest(fileParser, Lists.newArrayList("function", "a", "(", ")", "{", "\n", "local", " ", "local1"),
                FUNCTION_KEYWORD, WORD, LEFT_PAREN, RIGHT_PAREN, LEFT_CURLY, LINE_FEED,
                INTERNAL_COMMAND, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, LINE_FEED, RIGHT_CURLY);
    }
}
