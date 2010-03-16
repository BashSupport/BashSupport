/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ListParsingTest.java, Class: ListParsingTest
 * Last modified: 2010-03-16
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

import junit.framework.Assert;
import org.junit.Test;

/**
 * Date: 25.03.2009
 * Time: 13:25:48
 *
 * @author Joachim Ansorg
 */
public class ListParsingTest extends MockPsiTest {
    private MockFunction list1ParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.list.parseList1(builder, false, false);
        }
    };

    private MockFunction listSimpleParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.list.parseSimpleList(builder);
        }
    };

    private MockFunction compoundListParsingTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder builder) {
            return Parsing.list.parseCompoundList(builder, false, false, false);
        }
    };

    @Test
    public void testParseCompoundList() {
        mockTest(compoundListParsingTest,
                LINE_FEED, LINE_FEED, WORD, LINE_FEED); // \n \n \n a \n
    }

    @Test
    public void testParseCompoundList0() {
        mockTest(compoundListParsingTest, WORD, AMP); // a &
    }

    @Test
    public void testParseCompoundList1() {
        mockTest(compoundListParsingTest,
                LINE_FEED, LINE_FEED, WORD, SEMI); // \n \n \n a ;
    }

    @Test
    public void testParseCompoundList2() {
        mockTest(compoundListParsingTest,
                LINE_FEED, LINE_FEED, LINE_FEED, WORD, AND_AND, WORD, PIPE, WORD, AMP, WORD); // \n \n \n a && b | c & d
    }

    @Test
    public void testParseCompoundList3() {
        mockTest(compoundListParsingTest,
                LINE_FEED, LINE_FEED, LINE_FEED, WORD, AND_AND, WORD,
                PIPE, WORD, SEMI, LINE_FEED, LINE_FEED); // \n\n\n a && b | c ; \n \n
    }

    @Test
    public void testParseCompoundList5() {
        mockTest(compoundListParsingTest,
                WORD, SEMI); // a ;
    }

    @Test
    public void testParseCompoundListErrors() {
        mockTest(compoundListParsingTest, 2, WORD, AMP, SEMI); // a & ;
    }

    @Test
    public void parseList1_1() {
        mockTest(list1ParsingTest, WORD, AND_AND, LINE_FEED, WORD);
    }

    @Test
    public void testIsListTerminator() {
        Assert.assertTrue(Parsing.list.isListTerminator(LINE_FEED));
        Assert.assertTrue(Parsing.list.isListTerminator(SEMI));

        Assert.assertFalse(Parsing.list.isListTerminator(WORD));
        Assert.assertFalse(Parsing.list.isListTerminator(NUMBER));
        Assert.assertFalse(Parsing.list.isListTerminator(WHILE_KEYWORD));
    }

    @Test
    public void testParseList1Simple() {
        mockTest(list1ParsingTest, WORD);
    }

    @Test
    public void testParseList1SimpleWithNewlines() {
        mockTest(list1ParsingTest, WORD, LINE_FEED, LINE_FEED, LINE_FEED, WORD); // a \n\n\n c
    }

    @Test
    public void testParseList1Complex1() {
        mockTest(list1ParsingTest, WORD, AMP, WORD, PIPE, WORD, WORD); //a & b | c d
    }

    @Test
    public void testParseList1Error1() {
        mockTest(list1ParsingTest, 1, WORD, AMP, PIPE, WORD, WORD); //a & | c d
    }

    // simple list test

    @Test
    public void testParseSimpleList1() {
        mockTest(listSimpleParsingTest, WORD, WORD, SEMI, WORD, AMP); // a c
    }

    @Test
    public void testParseSimpleList2() {
        mockTest(listSimpleParsingTest, WORD, SEMI, WORD); // a c; d
    }

    @Test
    public void testParseSimpleList3() {
        mockTest(listSimpleParsingTest, WORD, WORD, SEMI, WORD, SEMI); // a c; d;
    }

    @Test
    public void testParseSimpleListError1() {
        mockTest(listSimpleParsingTest, 1, WORD, LINE_FEED, WORD, SEMI, WORD, SEMI); // a \n a; a;
    }

}
