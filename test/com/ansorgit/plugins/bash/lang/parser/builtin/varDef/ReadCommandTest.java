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

package com.ansorgit.plugins.bash.lang.parser.builtin.varDef;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.builtin.varDef.ReadCommand;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class ReadCommandTest extends MockPsiTest {
    private final MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            ReadCommand d = new ReadCommand();
            return d.parseIfValid(psi).isParsedSuccessfully();
        }
    };

    @Test
    public void testIsValid() {
        mockTest(parserFunction, Lists.newArrayList("read"), WORD);
        mockTest(parserFunction, Lists.newArrayList("read"), WORD, WORD);
        mockTest(parserFunction, Lists.newArrayList("read"), WORD, WORD, WORD, WORD);

        //read a v[i]
        mockTest(parserFunction, Lists.newArrayList("read"), WORD, WORD, ASSIGNMENT_WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE);
    }

    @Test
    public void testIssue125() throws Exception {
        //read 'test' a[0]
        mockTest(parserFunction, Lists.newArrayList("read"), WORD, STRING2, ASSIGNMENT_WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE);
    }

    @Test
    public void testBuiltin() {
        LanguageBuiltins.localVarDefCommands.contains("read");
    }
}
