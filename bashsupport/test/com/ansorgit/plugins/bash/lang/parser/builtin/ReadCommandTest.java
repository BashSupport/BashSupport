/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ReadCommandTest.java, Class: ReadCommandTest
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
 * User: jansorg
 * Date: 23.05.2009
 * Time: 23:51:45
 */
public class ReadCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            ReadCommand d = new ReadCommand();
            return d.parse(psi);
        }
    };

    @Test
    public void testIsValid() {
        mockTest(parserFunction, Lists.newArrayList("read"), INTERNAL_COMMAND);
        mockTest(parserFunction, Lists.newArrayList("read"), INTERNAL_COMMAND, WORD);
        mockTest(parserFunction, Lists.newArrayList("read"), INTERNAL_COMMAND, WORD, WORD, WORD);
    }

    @Test
    public void testBuiltin() {
        LanguageBuiltins.localVarDefCommands.contains("read");
    }
}
