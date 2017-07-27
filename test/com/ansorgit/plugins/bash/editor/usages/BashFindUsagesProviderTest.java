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

package com.ansorgit.plugins.bash.editor.usages;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.lang.findUsages.LanguageFindUsages;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class BashFindUsagesProviderTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testVarUsage() throws Exception {
        PsiElement element = configurePsiAtCaret();

        PsiElement varDef = element.getReference().resolve();
        Collection<UsageInfo> usages = myFixture.findUsages(varDef);
        Assert.assertEquals(7, usages.size());

        Assert.assertEquals("variable", typeNameFor(element));
        Assert.assertEquals("a", descriptiveNameFor(element));
    }

    @Test
    public void testVarDefUsage() throws Exception {
        PsiElement element = configurePsiAtCaret();

        Collection<UsageInfo> usages = myFixture.findUsages(element);
        Assert.assertEquals(3, usages.size());

        Assert.assertEquals("variable", typeNameFor(element));
        Assert.assertEquals("a", descriptiveNameFor(element));
    }

    @Test
    public void testHereDocUsage() throws Exception {
        PsiElement element = configurePsiAtCaret();

        Collection<UsageInfo> usages = myFixture.findUsages(element);
        Assert.assertEquals(1, usages.size());

        Assert.assertEquals("heredoc", typeNameFor(element));
        Assert.assertEquals("EOF", descriptiveNameFor(element));
    }

    @Test
    public void testFileUsage() throws Exception {
        PsiElement file = configurePsiAtCaret().getContainingFile();

        Collection<UsageInfo> usages = myFixture.findUsages(file);
        Assert.assertEquals(1, usages.size());

        Assert.assertEquals("Bash file", typeNameFor(file));
        Assert.assertEquals("fileUsage.bash", descriptiveNameFor(file));
    }

    @Test
    public void testFunctionNameUsage() throws Exception {
        configurePsiAtCaret();

        //getElementAtCaret to get the reference target instead of the word element inside of a reference
        PsiElement element = myFixture.getElementAtCaret();

        Collection<UsageInfo> usages = myFixture.findUsages(element);
        Assert.assertEquals(2, usages.size());

        Assert.assertEquals("function", typeNameFor(element));
        Assert.assertEquals("x", descriptiveNameFor(element));
    }

    @Test
    public void testFunctionUsage() throws Exception {
        configurePsiAtCaret();

        //getElementAtCaret to get the reference target instead of the word element inside of a reference
        PsiElement element = myFixture.getElementAtCaret();

        Collection<UsageInfo> usages = myFixture.findUsages(element);
        Assert.assertEquals(2, usages.size());

        Assert.assertEquals("function", typeNameFor(element));
        Assert.assertEquals("x", descriptiveNameFor(element));
    }

    @NotNull
    private String typeNameFor(PsiElement element) {
        return LanguageFindUsages.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE).getType(element);
    }

    @NotNull
    private String descriptiveNameFor(PsiElement element) {
        return LanguageFindUsages.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE).getDescriptiveName(element);
    }

    @NotNull
    @Override
    protected String getBasePath() {
        return "editor/usages";
    }

}