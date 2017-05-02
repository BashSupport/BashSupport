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

package com.ansorgit.plugins.bash.editor.accessDetector;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector.Access;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

/**
 */
public class BashReadWriteAccessDetectorTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testRead() throws Exception {
        PsiElement element = configurePsiAtCaret();

        ReadWriteAccessDetector detector = ReadWriteAccessDetector.findDetector(element);
        Assert.assertNotNull(detector);

        Assert.assertFalse(detector.isDeclarationWriteAccess(element));
        Assert.assertTrue(detector.isReadWriteAccessible(element));

        Assert.assertEquals("Expected read access", Access.Read, detector.getExpressionAccess(element));
    }

    @Test
    public void testWrite() throws Exception {
        PsiElement element = configurePsiAtCaret();

        ReadWriteAccessDetector detector = ReadWriteAccessDetector.findDetector(element);

        Assert.assertEquals("Expected write access", Access.Write, detector.getExpressionAccess(element));
        Assert.assertTrue(detector.isDeclarationWriteAccess(element));
    }

    @NotNull
    @Override
    protected String getBasePath() {
        return "editor/accessDetector";
    }
}