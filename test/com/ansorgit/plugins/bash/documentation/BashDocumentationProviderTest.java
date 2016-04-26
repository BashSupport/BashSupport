/*
 * Copyright (c) Joachim Ansorg, mail@ansorg-it.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.codeInsight.documentation.DocumentationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;
import org.junit.Test;

public class BashDocumentationProviderTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testFunctionComment() throws Exception {
        assertCaretElementDocumentation("Documentation for function a<br/>");
    }

    @Test
    public void testVarDefComment() throws Exception {
        assertCaretElementDocumentation("Documentation for varDef a<br/>");
    }

    @Test
    public void testWhileKeyword() throws Exception {
        String doc = findDocumentationForCaret();
        Assert.assertNotNull(doc);
    }

    @Test
    public void testIfKeyword() throws Exception {
        Assert.assertNotNull("Expected documentation for the internal command", findDocumentationForCaret());
    }

    @Test
    public void testExternalCommand1() {
        Assert.assertNotNull("Expected documentation for the external command", findDocumentationForCaret());
    }

    @Test
    public void testExternalCommand2() {
        Assert.assertNotNull(findDocumentationForCaret());
    }

    @Test
    public void testInfoCommandDoc() {
        //must be run on a command which doesn't have a pre-computed html file
        Assert.assertNotNull("Expected documentation content", findDocumentationForCaret());
    }

    private void assertCaretElementDocumentation(String expectedDocumentation) {
        String doc = findDocumentationForCaret();

        Assert.assertEquals("Expected comment to be returned as doc", expectedDocumentation, doc);
    }

    private String findDocumentationForCaret() {
        PsiElement originalElement = configurePsiAtCaret();
        Assert.assertNotNull(originalElement);

        PsiFile file = originalElement.getContainingFile();

        DocumentationManager manager = DocumentationManager.getInstance(getProject());
        PsiElement targetElement = manager.findTargetElement(myFixture.getEditor(), file, originalElement);
        if (targetElement instanceof PsiFile) {
            targetElement = null;
        }

        com.intellij.lang.documentation.DocumentationProvider provider = DocumentationManager.getProviderFromElement(targetElement, originalElement);
        Assert.assertNotNull(provider);

        String doc = provider.generateDoc(targetElement, originalElement);

        return "No documentation found.".equals(doc) ? null : doc;
    }

    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/editor/documentation/documentationProvider";
    }
}