/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: MockPsiTest.java, Class: MockPsiTest
 * Last modified: 2010-03-13
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
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.psi.tree.IElementType;
import junit.framework.Assert;

import java.util.Collections;
import java.util.List;

/**
 * This is the base class for the parser tests. It takes a mock parsing function and a set of tokens which
 * are passed to the function.
 * <p/>
 * Date: 24.03.2009
 * Time: 22:23:54
 *
 * @author Joachim Ansorg
 */
public abstract class MockPsiTest implements BashTokenTypes {
    protected MockPsiBuilder builderFor(List<String> textTokens, IElementType... elements) {
        return new MockPsiBuilder(textTokens, elements);
    }

    protected void assertNoErrors(MockPsiBuilder psi) {
        printErrors(psi);
        Assert.assertFalse("There are parsing errors: " + psi.getErrors(), psi.hasErrors());
    }

    protected void assertErrors(MockPsiBuilder psi) {
        printErrors(psi);
        Assert.assertTrue("There are no parsing errors", psi.hasErrors());
    }

    private void printErrors(MockPsiBuilder psi) {
        if (psi.hasErrors()) {
            System.err.println("Found PSI errors:");
            System.err.println("-----------------");

            final List<String> errors = psi.getErrors();
            for (String error : errors) {
                System.err.println("PSI error: " + error);
            }
        }
    }

    public void mockTest(MockFunction f, IElementType... elements) {
        mockTest(BashVersion.Bash_v3, f, elements.length, Collections.<String>emptyList(), elements);
    }

    public void mockTest(BashVersion version, MockFunction f, IElementType... elements) {
        mockTest(version, f, elements.length, Collections.<String>emptyList(), elements);
    }

    public void mockTest(MockFunction f, List<String> textTokens, IElementType... elements) {
        mockTest(f, elements.length, textTokens, elements);
    }

    public void mockTest(MockFunction f, int expectedCount, IElementType... elements) {
        mockTest(f, expectedCount, Collections.<String>emptyList(), elements);
    }

    public void mockTest(MockFunction f, int expectedCount, List<String> textTokens, IElementType... elements) {
        mockTest(BashVersion.Bash_v3, f, expectedCount, textTokens, elements);
    }

    public void mockTest(BashVersion version, MockFunction f, int expectedCount, List<String> textTokens, IElementType... elements) {
        final MockPsiBuilder mockBuilder = builderFor(textTokens, elements);
        BashPsiBuilder psi = new BashPsiBuilder(mockBuilder, version);

        Assert.assertTrue(f.preCheck(psi));

        boolean ok = f.apply(psi);
        assertNoErrors(mockBuilder);
        Assert.assertTrue("Parsing was not successful", ok);

        Assert.assertEquals("Not all elements have been processed.", expectedCount, mockBuilder.processedElements());
        Assert.assertTrue("Post condition failed", f.postCheck(mockBuilder));
    }

    public void mockTestError(MockFunction f, IElementType... elements) {
        mockTestError(BashVersion.Bash_v3, f, elements);
    }

    public void mockTestError(BashVersion version, MockFunction f, List<String> textTokens, IElementType... elements) {
        mockTestError(version, f, true, textTokens, elements);
    }

    public void mockTestError(BashVersion version, MockFunction f, boolean checkResult, List<String> textTokens, IElementType... elements) {
        MockPsiBuilder mockPsiBuilder = builderFor(textTokens, elements);
        BashPsiBuilder bashPsiBuilder = new BashPsiBuilder(mockPsiBuilder, version);

        boolean ok = f.apply(bashPsiBuilder);
        assertErrors(mockPsiBuilder);
        if (checkResult) {
            Assert.assertFalse(ok);
        }
    }

    public void mockTestError(BashVersion version, MockFunction f, IElementType... elements) {
        mockTestError(version, f, Collections.<String>emptyList(), elements);
    }

    public void mockTestFail(MockFunction f, IElementType... elements) {
        mockTestFail(BashVersion.Bash_v3, f, elements);
    }

    public void mockTestFail(BashVersion version, MockFunction f, IElementType... elements) {
        MockPsiBuilder mockPsiBuilder = builderFor(Collections.<String>emptyList(), elements);
        BashPsiBuilder bashPsiBuilder = new BashPsiBuilder(mockPsiBuilder, version);

        boolean ok = f.apply(bashPsiBuilder);
        Assert.assertFalse(ok);
    }

    public static abstract class MockFunction {
        public abstract boolean apply(BashPsiBuilder psi);

        public boolean preCheck(BashPsiBuilder psi) {
            return true;
        }

        public boolean postCheck(MockPsiBuilder mockBuilder) {
            return true;
        }
    }
}
