/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: PsiTests.java, Class: PsiTests
 * Last modified: 2010-04-24
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

import com.ansorgit.plugins.bash.lang.base.BashPsiTest;
import com.ansorgit.plugins.bash.lang.base.IntRef;
import com.ansorgit.plugins.bash.lang.base.TestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import junit.framework.Assert;
import org.jetbrains.annotations.NonNls;
import org.junit.Ignore;

import java.io.IOException;

/**
 * User: jansorg
 * Date: Jan 11, 2010
 * Time: 10:28:40 PM
 */
@Ignore
public class PsiTests extends BashPsiTest {
    @NonNls
    protected static final String START_MARKER = "#start#";
    @NonNls
    protected static final String STOP_MARKER = "#stop#";
    @NonNls
    protected static final String RESULT_MARKER = "#result#";

    private void doTest() throws IOException {
        String filename = getTestName(false).toLowerCase() + ".test";
        final VirtualFile file = getFile(filename, myModule);
        Assert.assertNotNull("File not found : " + filename, file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");
        final IntRef startMarker = new IntRef(fileText.indexOf(START_MARKER));
        final IntRef stopMarker = new IntRef(fileText.indexOf(STOP_MARKER));
        final IntRef resultMarker = new IntRef(fileText.indexOf(RESULT_MARKER));

        fileText = TestUtils.removeSubstring(fileText, startMarker.get(), START_MARKER.length(), startMarker, stopMarker, resultMarker);
        fileText = TestUtils.removeSubstring(fileText, stopMarker.get(), STOP_MARKER.length(), startMarker, stopMarker, resultMarker);
        fileText = TestUtils.removeSubstring(fileText, resultMarker.get(), RESULT_MARKER.length(), startMarker, stopMarker, resultMarker);

        final String result = fileText.substring(resultMarker.get());
        VfsUtil.saveText(file, fileText.substring(0, resultMarker.get()));

        final BashFile myFile = (BashFile) PsiManager.getInstance(myFixture.getProject()).findFile(file);
        final PsiElement startElement = myFile.findElementAt(startMarker.get());
        final PsiElement endElement = myFile.findElementAt(stopMarker.get());
        final BashPsiElement context = BashPsiUtils.getCoveringRPsiElement(PsiTreeUtil.findCommonParent(startElement, endElement));
        final StringBuffer buffer = new StringBuffer();
        //final Instruction[] instructions = new RControlFlowBuilder().buildControlFlow(myFile.getFileSymbol(), context, null, null);
        //for (Instruction instruction : instructions) {
        //            buffer.append(instruction).append("\n");
        //      }


        Assert.assertEquals(result.trim(), buffer.toString().trim());
    }

    public void testComment() throws IOException {
        doTest();
    }
}
