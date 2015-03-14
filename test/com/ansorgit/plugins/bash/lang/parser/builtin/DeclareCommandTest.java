/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: DeclareCommandTest.java, Class: DeclareCommandTest
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
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Date: 15.04.2009
 * Time: 22:55:48
 *
 * @author Joachim Ansorg
 */
public class DeclareCommandTest extends MockPsiTest {
    MockFunction declareParsing = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            DeclareCommand d = new DeclareCommand();
            return d.parse(psi);
        }
    };

    @Test
    public void testParse() {
        //declare a=1
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, WORD, EQ, WORD);
        //declare a=1 b c
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, WORD, EQ, WORD, WORD, WORD);
        //declare a=$b
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, WORD, EQ, VARIABLE);
        //declare a=$(echo 123)
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, WORD, EQ, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
        //declare $abc=$(echo 123)
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, VARIABLE, EQ, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
        //declare $abc=()
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, VARIABLE, EQ, LEFT_PAREN, RIGHT_PAREN);
        //declare $abc=(a)
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, VARIABLE, EQ, LEFT_PAREN, WORD, RIGHT_PAREN);
        //declare $abc=(a,b)
        mockTest(declareParsing, Lists.newArrayList("declare"), WORD, VARIABLE, EQ, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
    }

    @Test
    public void testBuiltin() {
        LanguageBuiltins.varDefCommands.contains("declare");
    }

}
