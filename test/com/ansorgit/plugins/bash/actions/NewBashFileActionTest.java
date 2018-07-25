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

package com.ansorgit.plugins.bash.actions;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("Duplicates")
public class NewBashFileActionTest extends BashCodeInsightFixtureTestCase {
    @Test
    public void testNewFile() throws Exception {
        ActionManager actionManager = ActionManager.getInstance();
        final NewBashFileAction action = (NewBashFileAction) actionManager.getAction("Bash.NewBashScript");

        // @see https://devnet.jetbrains.com/message/5539349#5539349
        VirtualFile directoryVirtualFile = myFixture.getTempDirFixture().findOrCreateDir("");
        final PsiDirectory directory = myFixture.getPsiManager().findDirectory(directoryVirtualFile);

        Assert.assertEquals(BashStrings.message("newfile.command.name"), action.getCommandName());
        Assert.assertEquals(BashStrings.message("newfile.menu.action.text"), action.getActionName(directory, ""));

        PsiElement result = ApplicationManager.getApplication().runWriteAction(new Computable<PsiElement>() {
            @Override
            public PsiElement compute() {
                try {
                    PsiElement[] elements = action.create("bash_file", directory);
                    return elements.length >= 1 ? elements[0] : null; //the firet element is the BashFile
                } catch (Exception e) {
                    return null;
                }
            }
        });

        assertNotNull("Expected a newly created bash file", result);
        assertTrue("Expected a newly created bash file", result instanceof BashFile);

        VirtualFile vFile = ((BashFile) result).getVirtualFile();
        File ioFile = VfsUtilCore.virtualToIoFile(vFile);
        assertTrue("Expected that the new file is executable", ioFile.canExecute());

        Assert.assertEquals("Expected default bash file template content", "#!/usr/bin/env bash", result.getText());
    }

    @Test
    public void testInvokeDialog() throws IOException {
        ActionManager actionManager = ActionManager.getInstance();
        final NewBashFileAction action = (NewBashFileAction) actionManager.getAction("Bash.NewBashScript");

        VirtualFile directoryVirtualFile = myFixture.getTempDirFixture().findOrCreateDir("");
        final PsiDirectory directory = myFixture.getPsiManager().findDirectory(directoryVirtualFile);

        try {
            action.invokeDialog(directory.getProject(), directory);
            Assert.fail("dialog wasn't invoked");
        } catch (Throwable e) {
            //the dialog invocation is raised as an exception in test mode
            Assert.assertTrue(e.getMessage().contains("Create a new Bash file"));
        }

    }
}