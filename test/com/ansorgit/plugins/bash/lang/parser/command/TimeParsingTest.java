/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: TimeParsingTest.java, Class: TimeParsingTest
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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import org.junit.Test;

/**
 * Date: 25.03.2009
 * Time: 13:22:51
 *
 * @author Joachim Ansorg
 */
public class TimeParsingTest extends MockPsiTest {
    private final MockFunction timeTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.pipeline.parseTimespec(psi);
        }
    };

    @Test
    public void testSimpleTime() {
        mockTest(timeTest, TIME_KEYWORD);
    }

    //fixme test the -p option, implement getTokenText for that    
}
