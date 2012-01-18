/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ExportCommandTest.java, Class: ExportCommandTest
 * Last modified: 2010-04-20
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.*;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;

import java.util.List;

/**
 * @author Joachim Ansorg
 */
public class UnsetCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.file.parseFile(psi);
        }
    };

    @Test
    public void testArrayAssignment() throws Exception {
        //unset todo_list[$todo_id]
        mockTest(parserFunction, WORD, WHITESPACE, ASSIGNMENT_WORD, LEFT_SQUARE, VARIABLE, RIGHT_SQUARE);
    }
}
