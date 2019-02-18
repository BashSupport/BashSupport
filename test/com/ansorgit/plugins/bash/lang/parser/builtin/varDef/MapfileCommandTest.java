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
import com.ansorgit.plugins.bash.lang.parser.builtin.varDef.MapfileCommand;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class MapfileCommandTest extends MockPsiTest {
    MockFunction parsingFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            MapfileCommand d = new MapfileCommand("mapfile");
            return d.parseIfValid(psi).isParsedSuccessfully();
        }
    };

    @Test
    public void testParse() {
        //mapfile a
        mockTest(BashVersion.Bash_v4, parsingFunction, 2, Lists.newArrayList("mapfile"), WORD, WORD);

        //mapfile -d x -s 10 a
        mockTest(BashVersion.Bash_v4, parsingFunction, 10, Lists.newArrayList("mapfile", "-d", " ", "x", " ", "-s", " ", "10", " ", "a"), WORD, WORD, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, WORD);
    }

    @Test
    public void testBuiltin() {
        Assert.assertTrue(LanguageBuiltins.varDefCommands.contains("mapfile"));
        Assert.assertTrue(LanguageBuiltins.varDefCommands.contains("mapfile"));

        Assert.assertFalse(LanguageBuiltins.commands.contains("mapfile"));
    }

}
