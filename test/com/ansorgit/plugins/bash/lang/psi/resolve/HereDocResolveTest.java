/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: HereDocResolveTest.java, Class: HereDocResolveTest
 * Last modified: 2010-07-01
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

package com.ansorgit.plugins.bash.lang.psi.resolve;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.intellij.psi.PsiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author jansorg
 */
public class HereDocResolveTest extends AbstractResolveTest {
    @Test
    public void testResolveHereDocStartMarker() throws Exception {
        PsiReference endMarkerRef = configure();
        Assert.assertNotNull(endMarkerRef);

        Assert.assertTrue(endMarkerRef.resolve() instanceof BashHereDocStartMarker);
    }

    @Test
    public void testResolveHereDocStartMarkerWithEval() throws Exception {
        PsiReference endMarkerRef = configure();
        Assert.assertNotNull(endMarkerRef);

        Assert.assertTrue(endMarkerRef.resolve() instanceof BashHereDocStartMarker);
    }

    @Test
    public void testResolveHereDocEndMarker() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertNotNull(startMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    @Test
    public void testResolveHereDocEndMarkerWithEval() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertNotNull(startMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    @Test
    public void testResolveErrorFile() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertNotNull(startMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    @Test
    public void testResolveErrorFile2() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertNotNull(startMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/hereDoc/";
    }
}
