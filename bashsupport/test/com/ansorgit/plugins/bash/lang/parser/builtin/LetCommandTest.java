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
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.MockPsiTest;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.tree.IElementType;
import org.junit.Test;

import java.util.List;

/**
 * Date: 01.05.2009
 * Time: 21:16:12
 *
 * @author Joachim Ansorg
 */
public class LetCommandTest extends MockPsiTest {
    MockFunction parserFunction = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new LetCommand().parse(psi);
        }
    };

    MockFunction parserFunctionWithMarker = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return new LetCommand().parse(psi);
        }

        @Override
        public boolean postCheck(MockPsiBuilder mockBuilder) {
            List<Pair<MockPsiBuilder.MockMarker, IElementType>> markers = mockBuilder.getDoneMarkers();
            if (markers.size() == 0) {
                return false;
            }

            //has to contain at least one variable def element marker
            for (Pair<MockPsiBuilder.MockMarker, IElementType> marker : markers) {
                if (marker.getSecond() == BashElementTypes.VAR_DEF_ELEMENT) {
                    return true;
                }
            }

            return false;
        }
    };

    @Test
    public void testBuiltin() {
        LanguageBuiltins.arithmeticCommands.contains("let");
    }

    @Test
    public void testParse() {
        //let a=1
        mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER);

        //let a=1+1
        mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER, ARITH_PLUS, NUMBER);

        //let a=1 b=1
        mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER, WHITESPACE, ASSIGNMENT_WORD, EQ, NUMBER);

        //let a=1 b=1
        mockTest(parserFunction, Lists.newArrayList("let"), WORD, WHITESPACE, STRING_BEGIN, ASSIGNMENT_WORD, EQ, NUMBER, STRING_END);
    }
}
