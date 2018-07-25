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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.command.AbstractBashCommand;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.injection.Injectable;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.plugins.intelliLang.inject.InjectLanguageAction;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class InjectionReferenceTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testInjectedVarReference() throws Exception {
        PsiElement varReference = findInjectedBashReference("VarInjection.java", "$X");

        PsiElement target = varReference.getReference().resolve();
        Assert.assertTrue(target instanceof BashVarDef);
        Assert.assertEquals("X", ((BashVarDef) target).getName());
    }

    @Test
    public void testInjectedFunctionReference() throws Exception {
        PsiElement functionReference = findInjectedBashReference("FunctionInjection.java", "myFunc arg");

        Assert.assertTrue(functionReference instanceof AbstractBashCommand);

        PsiElement target = functionReference.getReference().resolve();
        Assert.assertTrue(target instanceof BashFunctionDef);
        Assert.assertEquals("myFunc", ((BashFunctionDef) target).getName());
    }

    @NotNull
    private PsiElement findInjectedBashReference(String fileName, String lookupText) {
        PsiElement javaLiteral = configurePsiAtCaret(fileName);
        Assert.assertTrue(javaLiteral instanceof PsiLanguageInjectionHost);

        //inject bash into the literal
        InjectLanguageAction.invokeImpl(getProject(), myFixture.getEditor(), javaLiteral.getContainingFile(), Injectable.fromLanguage(BashFileType.BASH_LANGUAGE));

        String fileContent = javaLiteral.getContainingFile().getText();
        PsiElement bashPsiLeaf = InjectedLanguageManager.getInstance(getProject()).findInjectedElementAt(myFixture.getFile(), fileContent.indexOf(lookupText) + 1);
        Assert.assertNotNull(bashPsiLeaf);

        PsiElement reference = PsiTreeUtil.findFirstParent(bashPsiLeaf, psiElement -> psiElement.getReference() != null);
        Assert.assertNotNull(reference);

        return reference;
    }

    @Override
    protected String getBasePath() {
        return "psi/resolve/injection";
    }
}
