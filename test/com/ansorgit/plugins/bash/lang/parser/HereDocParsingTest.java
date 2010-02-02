/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: HereDocParsingTest.java, Class: HereDocParsingTest
 * Last modified: 2010-01-29
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

package com.ansorgit.plugins.bash.lang.parser;

import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * Date: 11.04.2009
 * Time: 21:28:42
 *
 * @author Joachim Ansorg
 */
public class HereDocParsingTest extends MockPsiTest {
    MockFunction hereDoc = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.file.parseFile(psi);
        }
    };

    @Test
    public void testSimpleHereDocs() {
        //a << END
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "END"),
                WORD, REDIRECT_LESS_LESS, WORD, LINE_FEED, WORD);

        //a <<- END
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "END"),
                WORD, REDIRECT_LESS_LESS_MINUS, WORD, LINE_FEED, WORD);

        //a <<- END
        // TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "END", "\n", "TEST", "\n", "END"),
                WORD, REDIRECT_LESS_LESS_MINUS, WORD, LINE_FEED, WORD, LINE_FEED, WORD);

        //a <<-"END"
        // TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "\"", "END", "\"", "\n", "TEST", "\n", "END"),
                WORD, REDIRECT_LESS_LESS_MINUS, STRING_BEGIN, WORD, STRING_END, LINE_FEED, WORD, LINE_FEED, WORD);
    }

    @Test
    public void testComplicatedHereDocs() {
        //a <<END
        // $TEST ()
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "$TEST", "\n", "END"),
                WORD, REDIRECT_LESS_LESS_MINUS, WORD, LINE_FEED, VARIABLE, LINE_FEED, WORD);
    }
}
