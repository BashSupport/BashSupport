/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFullPsiTest.java, Class: BashFullPsiTest
 * Last modified: 2010-02-01   
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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.lang.lexer.BashLexer;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.FileParsing;
import com.ansorgit.plugins.bash.lang.parser.MockPsiBuilder;
import com.google.common.collect.Lists;
import com.intellij.psi.tree.IElementType;
import org.junit.Assert;
import org.junit.Ignore;

import java.util.List;

/**
 * User: jansorg
 * Date: Feb 1, 2010
 * Time: 9:49:03 PM
 */
@Ignore
public class BashFullPsiTest {
    public void testFull(BashVersion bashVersion, String data, Object expected) {
        BashLexer lexer = new BashLexer(bashVersion);

        lexer.start(data);

        //read in all tokens
        List<String> tokenTexts = Lists.newArrayList();
        List<IElementType> tokenTypes = Lists.newArrayList();

        IElementType current;
        while ((current = lexer.getTokenType()) != null) {
            tokenTypes.add(current);
            tokenTexts.add(lexer.getTokenText());
        }

        StringBuilder textResult = new StringBuilder();
        MockPsiBuilder psiBuilder = new MockPsiBuilder(tokenTexts, tokenTypes.toArray(new IElementType[tokenTypes.size()]));
        BashPsiBuilder b = new BashPsiBuilder(psiBuilder, bashVersion);
        FileParsing fileParsing = new FileParsing();
        fileParsing.parseFile(b);

        //unfinished code
        Assert.assertEquals(expected, psiBuilder.resultText());
    }
}
