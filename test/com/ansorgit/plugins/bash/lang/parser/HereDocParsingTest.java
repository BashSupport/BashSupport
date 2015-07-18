/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: HereDocParsingTest.java, Class: HereDocParsingTest
 * Last modified: 2010-06-06
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
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_MARKER_END);

        //a <<- END
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_MARKER_END);

        //a <<- END
        // TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "END", "\n", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, LINE_FEED, HEREDOC_MARKER_END);

        //a <<-"END"
        // TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "\"END\"", "\n", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, LINE_FEED, HEREDOC_MARKER_END);

        //a <<-"END"
        // "TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "\"END\"", "\n", "\"", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);

        //a <<-'END'
        // "TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "'END'", "\n", "\"", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);
    }

    @Test
    public void testEmbeddedVars() throws Exception {
        //a << END
        //$a
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "$a", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, VARIABLE, HEREDOC_CONTENT, HEREDOC_MARKER_END);

        //a << END
        //${a}
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "$", "{", "a", "}", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, LINE_FEED, HEREDOC_MARKER_END);
    }

    @Test
    public void testComplicatedHereDocs() {
        //a <<END
        // $TEST ()
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "$", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);
    }


    @Test
    public void testHereDocMarkerExclamation() throws Exception {
        //  x << !
        //   Text
        // !

        mockTest(hereDoc, Lists.newArrayList("x", "<<", "!", "\n", "Text", "\n", "!"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END
        );
    }

    @Test
    public void testHereDocVar() throws Exception {
        //  x << X
        //   abc
        //   $abc
        // X
        mockTest(hereDoc, Lists.newArrayList("x", "<<", "X", "\n", "abc", "$abc", "\n", "X"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, VARIABLE, HEREDOC_MARKER_END
        );

        //  x << X
        //   $abc
        // X
        mockTest(hereDoc, Lists.newArrayList("x", "<<", "X", "\n", "$abc", "\n", "X"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, VARIABLE, HEREDOC_MARKER_END
        );
    }
}
