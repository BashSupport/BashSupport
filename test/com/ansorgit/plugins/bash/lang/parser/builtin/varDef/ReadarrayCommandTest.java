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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class ReadarrayCommandTest extends MockPsiTest {
    MockFunction parsingFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            ReadarrayCommand d = new ReadarrayCommand();
            return d.parseIfValid(psi).isParsedSuccessfully();
        }
    };

    @Test
    public void testParse() {
        //readarray a
        mockTest(BashVersion.Bash_v4, parsingFunction, 2, Lists.newArrayList("readarray"), WORD, WORD);
        //readarray -td '' a
        mockTest(BashVersion.Bash_v4, parsingFunction, 5, Lists.newArrayList("readarray", "-td", " ", "''", "a"), WORD, WORD, WHITESPACE, WORD, WORD);
    }

    @Test
    public void testBuiltin() {
        Assert.assertTrue(LanguageBuiltins.varDefCommands.contains("readarray"));
        Assert.assertTrue(LanguageBuiltins.varDefCommands.contains("readarray"));

        Assert.assertFalse(LanguageBuiltins.commands.contains("readarray"));
    }
}
