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

package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import org.junit.Test;

/**
 */
public class BashHighlightingLexerTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testLexerHighlighting() throws Exception {
        //test #398, which had a broken lexer which broke the file highlighting with errors after new text was entered

        myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "$(<caret>)");

        myFixture.type("$");
        myFixture.type("{");
        myFixture.type("1");
        myFixture.type("}"); //typing these characters resulted in lexer exceptions all over the place
    }
}
