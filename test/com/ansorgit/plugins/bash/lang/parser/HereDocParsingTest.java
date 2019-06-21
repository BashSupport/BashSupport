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

package com.ansorgit.plugins.bash.lang.parser;

import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class HereDocParsingTest extends MockPsiTest {
    private MockFunction hereDoc = new MockFunction() {
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
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, LINE_FEED, HEREDOC_MARKER_IGNORING_TABS_END);

        //a <<-"END"
        // TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "\"END\"", "\n", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, LINE_FEED, HEREDOC_MARKER_IGNORING_TABS_END);

        //a <<-"END"
        // "TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "\"END\"", "\n", "\"", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_IGNORING_TABS_END);

        //a <<-'END'
        // "TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<-", "'END'", "\n", "\"", "TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_IGNORING_TABS_END);
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
        //$TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "$TEST", "\n", "END"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);

        //an escaped heredoc variable
        //a <<END
        //\$TEST
        //END
        mockTest(hereDoc,
                Lists.newArrayList("a", "<<", "END", "\n", "\\$TEST", "\n", "END"),
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
    public void testHereWhitespacePrefix() throws Exception {
        //  x <<- EOL
        //  \t\tText
        // EOL
        mockTest(hereDoc, Lists.newArrayList("x", "<<-", "EOL", "\n", "\t\tText", "\n", "EOL"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_IGNORING_TABS_END
        );

        //  x <<- EOL
        //  \t\tText
        // \tEOL
        mockTest(hereDoc, Lists.newArrayList("x", "<<-", "EOL", "\n", "\t\tText", "\n", "\tEOL"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_IGNORING_TABS_END
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

        //  x << X << Y
        //   $abc
        // X
        // Y
        mockTest(hereDoc, Lists.newArrayList("x", "<<", "X", "<<", "Y", "\n", "$abc", "\n", "X", "\n", "Y"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, VARIABLE, HEREDOC_MARKER_END, HEREDOC_MARKER_END
        );
    }

    @Test
    public void testNestedInIf() throws Exception {
        //if test "X$1"; then
        //cat <<XX
        //XX
        //echo
        //fi
        mockTest(hereDoc, Lists.newArrayList("if", "test", "'X'", ";", "THEN", "\n", "cat", "<<", "XX", "\n", "XX", "\n", "echo", "\n", "fi"),
                IF_KEYWORD, WORD, STRING2, SEMI, THEN_KEYWORD, LINE_FEED,
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED,
                HEREDOC_MARKER_END, LINE_FEED,
                WORD, LINE_FEED,
                FI_KEYWORD
        );
    }

    @Test
    public void testBackquteHeredoc() throws Exception {
        //`cat <<EOF
        // X
        // EOF`
        // X
        mockTest(hereDoc,
                Lists.newArrayList("`", "cat", "<<", "EOF", "\n", "X", "EOF", "`", "\n", "X"),
                BACKQUOTE, WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END, BACKQUOTE, LINE_FEED, WORD
        );
    }

    @Test
    public void testTrailingSemicolon() throws Exception {
        //issue 474
        mockTest(hereDoc, Lists.newArrayList("cat", "<<", "EOF", ";", "\n", "X", "EOF"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, SEMI, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);

        mockTest(hereDoc, Lists.newArrayList("cat", "<<", "EOF", "&", "\n", "X", "EOF"),
                WORD, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, AMP, LINE_FEED, HEREDOC_CONTENT, HEREDOC_MARKER_END);
    }

    @Test
    public void testIssue682() {
        // cat > file <<-EOF ||
        // EOF
        // echo failed
        mockTest(hereDoc, Lists.newArrayList("cat", ">", "file", "<<-", "EOF", "||", "\n", "EOF", "echo", "failed"),
                WORD, WHITESPACE, GREATER_THAN, WHITESPACE, WORD, WHITESPACE, HEREDOC_MARKER_TAG, HEREDOC_MARKER_START, WHITESPACE, OR_OR, LINE_FEED,
                HEREDOC_MARKER_IGNORING_TABS_END, LINE_FEED,
                WORD, WHITESPACE, WORD);
    }
}
