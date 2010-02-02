/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: CaseParsingTest.java, Class: CaseParsingTest
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
 * Date: 26.03.2009
 * Time: 11:57:52
 *
 * @author Joachim Ansorg
 */
public class CaseParsingTest extends MockPsiTest {
    private final MockFunction caseTest = new MockFunction() {
        @Override
        public boolean apply(BashPsiBuilder psi) {
            return Parsing.shellCommand.caseParser.parse(psi);
        }
    };

    @Test
    public void testCaseSimple1() {
        //case a in a) echo a; esac
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, WORD, RIGHT_PAREN,
                WORD, WORD, SEMI, ESAC_KEYWORD
        );
    }

    @Test
    public void testCaseSimple2() {
        //case a in (a) echo a; esac 
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, LEFT_PAREN, WORD, RIGHT_PAREN,
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
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD,
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
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, LINE_FEED,
                LEFT_PAREN, WORD, RIGHT_PAREN, LINE_FEED,
                CASE_END, LINE_FEED,
                ESAC_KEYWORD
        );
    }

    @Test
    public void testCaseSimple5() {
        //case a in esac
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple6() {
        //case a in a) esac
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, WORD, RIGHT_PAREN, ESAC_KEYWORD);
    }

    @Test
    public void testCaseSimple7() {
        //case a in a) echo a;; esac
        mockTest(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, WORD, RIGHT_PAREN, WORD, WORD, CASE_END,
                ESAC_KEYWORD);
    }

    @Test
    public void testCaseError1() {
        //case a in ;; esac
        mockTestError(caseTest,
                CASE_KEYWORD, WORD, IN_KEYWORD, CASE_END, ESAC_KEYWORD);
    }

    @Test
    public void testCaseError2() {
        //case esac
        mockTestError(caseTest,
                CASE_KEYWORD, ESAC_KEYWORD
        );
    }
}
