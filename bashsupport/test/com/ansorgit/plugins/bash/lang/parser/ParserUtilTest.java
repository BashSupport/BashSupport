/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ParserUtilTest.java, Class: ParserUtilTest
 * Last modified: 2010-10-05
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

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import org.junit.Assert;
import org.junit.Test;

import static com.ansorgit.plugins.bash.lang.BashVersion.Bash_v3;
import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.*;

/**
 * Date: 26.03.2009
 * Time: 12:26:52
 *
 * @author Joachim Ansorg
 */
public class ParserUtilTest {
    @Test
    public void testIsWordToken() {
        Assert.assertTrue(ParserUtil.isWordToken(WORD));

        Assert.assertFalse(ParserUtil.isWordToken(VARIABLE));
        Assert.assertFalse(ParserUtil.isWordToken(FOR_KEYWORD));
    }

    @Test
    public void testCheckNext() {
        BashPsiBuilder b = new BashPsiBuilder(null, new MockPsiBuilder(BACKQUOTE, BACKQUOTE), Bash_v3);
        Assert.assertTrue(ParserUtil.hasNextTokens(b, false, BACKQUOTE));
        Assert.assertTrue(ParserUtil.hasNextTokens(b, false, BACKQUOTE, BACKQUOTE));
        Assert.assertFalse(ParserUtil.hasNextTokens(b, false, WORD));
    }

    @Test
    public void testLookahead() {
        BashPsiBuilder b = new BashPsiBuilder(null, new MockPsiBuilder(WORD, WHITESPACE, WHITESPACE,  NUMBER), Bash_v3);
        Assert.assertEquals(WORD, b.lookAhead(0));
        Assert.assertEquals(NUMBER, b.lookAhead(1));
    }

    @Test
    public void testLookaheadRaw() {
        BashPsiBuilder b = new BashPsiBuilder(null, new MockPsiBuilder(WORD, WHITESPACE, WHITESPACE,  NUMBER), Bash_v3);
        Assert.assertEquals(WORD, b.rawLookup(0));
        Assert.assertEquals(WHITESPACE, b.rawLookup(1));
        Assert.assertEquals(WHITESPACE, b.rawLookup(2));
        Assert.assertEquals(NUMBER, b.rawLookup(3));
    }
}
