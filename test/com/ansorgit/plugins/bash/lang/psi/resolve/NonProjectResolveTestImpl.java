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

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 */
public class NonProjectResolveTestImpl extends AbstractResolveTest {

    @Test
    public void testNonProjectFile() throws Exception {
        String content = "export X=\necho $X";
        PsiFile psiFile = createTempPsiFile(content);

        PsiElement var = psiFile.findElementAt(content.indexOf("$X") + 1);
        Assert.assertNotNull(var);
        Assert.assertEquals("$X", var.getText());

        PsiReference reference = var.getParent().getReference();
        Assert.assertNotNull(reference);
        Assert.assertEquals("X", reference.getCanonicalText());

        PsiElement varDef = reference.resolve();
        Assert.assertNotNull(varDef);
        Assert.assertTrue(varDef instanceof BashVarDef);
        Assert.assertNotNull(((BashVarDef) varDef).getName());
    }

    @Test
    public void testFunctionResolve() throws Exception {
        String content = "function myFunc {\n echo hi\n}\nmyFunc";
        PsiFile file = createTempPsiFile(content);

        PsiReference reference = file.findReferenceAt(content.indexOf("\nmyFunc") + 2);
        Assert.assertNotNull(reference);

        PsiElement target = reference.resolve();
        Assert.assertNotNull(target);
        Assert.assertTrue(target instanceof BashFunctionDef);
        Assert.assertEquals("myFunc", ((BashFunctionDef) target).getName());
    }

    @NotNull
    private PsiFile createTempPsiFile(@NotNull String content) throws IOException {
        File tempFile = File.createTempFile("test", ".bash");
        FileUtil.writeToFile(tempFile, content);

        final VirtualFile vFile = LocalFileSystem.getInstance().findFileByIoFile(tempFile);
        assertNotNull("file " + tempFile.getAbsolutePath() + " not found", vFile);

        Assert.assertEquals(BashFileType.BASH_FILE_TYPE, vFile.getFileType());

        PsiFile psiFile = PsiManager.getInstance(myProject).findFile(vFile);
        Assert.assertNotNull(psiFile);

        return psiFile;
    }
}
