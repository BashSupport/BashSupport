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

package com.ansorgit.plugins.bash.intelliLang;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

public class BashLanguageInjectionTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testSingleQuotedInjection() throws Exception {
        assertIsValidInjectionHost("''", BashWord.class);
        assertIsValidInjectionHost("'text'", BashWord.class);
        assertIsValidInjectionHost("'more text'", BashWord.class);
    }

    @Test
    public void testDoubleQuotedInjection() throws Exception {
        assertIsValidInjectionHost("\"\"", BashString.class);
        assertIsValidInjectionHost("\"text\"", BashString.class);
        assertIsValidInjectionHost("\"more text\"", BashString.class);
    }

    public void testInvalidHost() throws Exception {
        assertIsNotInjectionHost("command", BashCommand.class);

        assertIsInvalidInjectionHost("command", BashWord.class);
    }

    private void assertIsNotInjectionHost(String fileContent, Class<? extends PsiElement> elementClass) {
        PsiElement element = fileElementOfType(fileContent, elementClass);
        Assert.assertFalse("The element must not be an injection host", element instanceof PsiLanguageInjectionHost);
    }

    private void assertIsInvalidInjectionHost(String fileContent, Class<? extends PsiElement> elementClass) {
        PsiElement element = fileElementOfType(fileContent, elementClass);
        Assert.assertTrue("The element must be an injection host", element instanceof PsiLanguageInjectionHost);
        Assert.assertFalse("The element must not be a valid injection host", ((PsiLanguageInjectionHost) element).isValidHost());
    }

    private void assertIsValidInjectionHost(String fileContent, Class<? extends PsiElement> elementClass) {
        PsiElement element = fileElementOfType(fileContent, elementClass);
        Assert.assertTrue("The element must be an injection host", element instanceof PsiLanguageInjectionHost);
        Assert.assertTrue("The element must be a valid injection host", ((PsiLanguageInjectionHost) element).isValidHost());
    }

    @NotNull
    private PsiElement fileElementOfType(String fileContent, Class<? extends PsiElement> elementClass) {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, fileContent);

        PsiElement element = PsiTreeUtil.findChildOfType(file, elementClass);
        Assert.assertNotNull(element);
        return element;
    }

    public void testSyntaxHighlighterFactory() throws Exception {
        PsiFile file = myFixture.configureByText(BashFileType.BASH_FILE_TYPE, "echo");
        SyntaxHighlighter syntaxHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(BashFileType.BASH_FILE_TYPE, getProject(), file.getVirtualFile());

        Assert.assertNotNull(syntaxHighlighter);
    }
}