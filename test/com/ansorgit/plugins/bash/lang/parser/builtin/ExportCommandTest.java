/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ExportCommandTest.java, Class: ExportCommandTest
 * Last modified: 2010-02-10
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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Date: 01.05.2009
 * Time: 21:16:12
 *
 * @author Joachim Ansorg
 */
public class ExportCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            ExportCommand d = new ExportCommand();
            return d.parse(psi);
        }
    };

    @Test
    public void testParse() {
        //export a=1
        mockTest(parserFunction, Lists.newArrayList("export"), INTERNAL_COMMAND, ASSIGNMENT_WORD, EQ, WORD);
        //export a
        mockTest(parserFunction, Lists.newArrayList("export"), INTERNAL_COMMAND, WORD);
        //export a=1 b=2
        mockTest(parserFunction, Lists.newArrayList("export"),
                INTERNAL_COMMAND, ASSIGNMENT_WORD, EQ, WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, WORD);
    }

    @Test
    public void testComplicated() {
        //>out a=1 export a=1
        mockTest(parserFunction, Lists.newArrayList(">", "out", " ", "a", "=", "1", " ", "export"),
                GREATER_THAN, WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER, WHITESPACE, INTERNAL_COMMAND,
                WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER);
    }
}
