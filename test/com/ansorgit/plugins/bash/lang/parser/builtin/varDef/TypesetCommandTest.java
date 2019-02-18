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
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class TypesetCommandTest extends MockPsiTest {
    MockFunction typeset = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            TypesetCommand d = new TypesetCommand();
            return d.parseIfValid(psi).isParsedSuccessfully();
        }
    };

    @Test
    public void testParse() {
        //declare a=1
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, WORD, EQ, WORD);
        //declare a=1 b c
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, WORD, EQ, WORD, WORD, WORD);
        //declare a=$b
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, WORD, EQ, VARIABLE);
        //declare a=$(echo 123)
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, WORD, EQ, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
        //declare $abc=$(echo 123)
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, VARIABLE, EQ, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
        //declare $abc=()
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, VARIABLE, EQ, LEFT_PAREN, RIGHT_PAREN);
        //declare $abc=(a)
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, VARIABLE, EQ, LEFT_PAREN, WORD, RIGHT_PAREN);
        //declare $abc=(a,b)
        mockTest(typeset, Lists.newArrayList("typeset"), WORD, VARIABLE, EQ, LEFT_PAREN, WORD, WORD, RIGHT_PAREN);
    }

    @Test
    public void testBuiltin() {
        LanguageBuiltins.varDefCommands.contains("typeset");
    }

}
