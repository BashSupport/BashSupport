/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: CaseParsingFunctionTest.java, Class: CaseParsingFunctionTest
 * Last modified: 2013-04-13
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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.google.common.collect.Lists;
import org.junit.Test;

/**
 * @author jansorg
 */
public class CaseParsingFunctionTest extends MockPsiTest {
    private final MockFunction caseTest = new MockFunction() {
        @Override
        public boolean preCheck(BashPsiBuilder psi) {
            return Parsing.shellCommand.caseParser.isValid(psi);
        }

        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.caseParser.parse(psi);
        }
    };

    @Test
    public void testCaseSimple1() {
        //case a in a) echo a; esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in", "a)"),
                CASE_KEYWORD, WORD, WORD, WORD, RIGHT_PAREN,
                WORD, WORD, SEMI, ESAC_KEYWORD
        );
    }

    @Test
    public void testCaseSimple2() {
        //case a in (a) echo a; esac 
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, LEFT_PAREN, WORD, RIGHT_PAREN,
                WORD, WORD, SEMI, ESAC_KEYWORD
        );
    }

    @Test
    public void testCaseSimple3() {
        //case a in
        // (a) echo a
        // ;;
        // b) echo b
        //
        // esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD,
                LEFT_PAREN, WORD, RIGHT_PAREN, WORD, WORD, LINE_FEED,
                CASE_END, LINE_FEED,
                WORD, RIGHT_PAREN, WORD, WORD, LINE_FEED,
                LINE_FEED,
                ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple4() {
        //case a in
        // (a)
        // ;;
        // esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, LINE_FEED,
                LEFT_PAREN, WORD, RIGHT_PAREN, LINE_FEED,
                CASE_END, LINE_FEED,
                ESAC_KEYWORD
        );
    }

    @Test
    public void testCaseSimple5() {
        //case a in esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple6() {
        //case a in a) esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, WORD, RIGHT_PAREN, ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple7() {
        //case a in a) echo a;; esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, WORD, RIGHT_PAREN, WORD, WORD, CASE_END,
                ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple8() {
        //case a in a) echo a;; esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, WORD, RIGHT_PAREN, WORD, WORD, CASE_END,
                ESAC_KEYWORD);
    }

    @Test
    public void testMultiwordPattern() throws Exception {
        //case a in "a b") echo a;; esac
        mockTest(caseTest, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, STRING_BEGIN, STRING_CONTENT, WHITESPACE, STRING_CONTENT, STRING_END, RIGHT_PAREN, WORD, WORD, CASE_END, ESAC_KEYWORD);

    }

    @Test
    public void testIssue118() throws Exception {
        // "case x in\n\t.+(a|b)) echo;; esac"
        mockTest(caseTest, Lists.newArrayList("case", "", "a", "", "in"),
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, WORD, LINE_FEED,
                WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, WORD, CASE_END, WHITESPACE, ESAC_KEYWORD);
    }

    @Test
    public void testCaseError1() {
        //case a in ;; esac
        mockTestError(BashVersion.Bash_v4, caseTest, true, false, Lists.newArrayList("case", "a", "in"),
                CASE_KEYWORD, WORD, WORD, CASE_END, ESAC_KEYWORD);
    }

    @Test
    public void testCaseError2() {
        //case esac
        mockTestError(caseTest,
                CASE_KEYWORD, ESAC_KEYWORD
        );
    }
}
