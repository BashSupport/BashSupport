/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: PiplelineParsingTest.java, Class: PiplelineParsingTest
 * Last modified: 2009-12-04
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

import org.junit.Test;

/**
 * Date: 25.03.2009
 * Time: 12:27:01
 *
 * @author Joachim Ansorg
 */
public class PiplelineParsingTest extends MockPsiTest {
    private final MockFunction testPipeline = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.pipeline.parsePipelineCommand(psi);
        }
    };

    @Test
    public void testParsePipelineCommand1() {
        //time;
        mockTest(testPipeline, TIME_KEYWORD, SEMI);
    }

    @Test
    public void testParsePipelineCommand2() {
        //echo
        mockTest(testPipeline, WORD);
    }

    @Test
    public void testParsePipelineCommand3() {
        //! time echo a
        mockTest(testPipeline, BANG_TOKEN, TIME_KEYWORD, WORD, WORD);
    }

    @Test
    public void testParsePipelineCommand4() {
        //! time echo a | echo b
        mockTest(testPipeline, BANG_TOKEN, TIME_KEYWORD, WORD, WORD, PIPE, WORD, WORD);
    }

    @Test
    public void testParseBashV4() {
        //! time echo a |& cat -
        mockTest(testPipeline, BANG_TOKEN, TIME_KEYWORD, WORD, WORD, PIPE_AMP, WORD, WORD);
    }
}
