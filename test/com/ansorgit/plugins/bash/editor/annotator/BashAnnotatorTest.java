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

package com.ansorgit.plugins.bash.editor.annotator;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.codeInsight.daemon.LightDaemonAnalyzerTestCase;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class BashAnnotatorTest extends LightDaemonAnalyzerTestCase {
    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    /**
     * https://code.google.com/p/bashsupport/issues/detail?id=192
     */
    @Test
    public void testArithmeticHighlighingNPE() {
        configureByFile("/editor/annotator/arithmeticHighlighting.bash");
        doHighlighting();
    }

    @Test
    public void testValidIdentifiers() {
        configureByFile("/editor/annotator/validIdentifiers.bash");
        doHighlighting();

        List<HighlightInfo> errors = highlightErrors();
        Assert.assertEquals("Errors: " + errors, 0, errors.size());
    }

    @Test
    public void testIdentifiers() {
        configureByFile("/editor/annotator/identifiers.bash");
        doHighlighting();

        List<HighlightInfo> errors = highlightErrors();
        Assert.assertEquals("Errors: " + errors, 5, errors.size());
    }

    public void testEvalVarDef() throws Exception {
        configureByFile("/editor/annotator/449-eval-vardef.bash");
        doHighlighting();

        List<HighlightInfo> errors = highlightErrors();
        Assert.assertEquals(0, errors.size());
    }
}