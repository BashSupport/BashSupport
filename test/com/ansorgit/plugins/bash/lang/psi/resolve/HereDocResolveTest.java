/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: HereDocResolveTest.java, Class: HereDocResolveTest
 * Last modified: 2010-06-30
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

import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.intellij.psi.PsiReference;
import junit.framework.Assert;

/**
 * User: jansorg
 * Date: 30.06.2010
 * Time: 20:51:09
 */
public class HereDocResolveTest extends AbstractResolveTest {
    public void testResolveHereDocStartMarker() throws Exception {
        PsiReference endMarker = configure();
        Assert.assertTrue(endMarker instanceof BashHereDocEndMarker);
        Assert.assertTrue(endMarker.resolve() instanceof BashHereDocStartMarker);
    }

    public void testResolveHereDocStartMarkerWithEval() throws Exception {
        PsiReference endMarker = configure();
        Assert.assertTrue(endMarker instanceof BashHereDocEndMarker);
        Assert.assertTrue(endMarker.resolve() instanceof BashHereDocStartMarker);
    }

    public void testResolveHereDocEndMarker() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertTrue(startMarker instanceof BashHereDocStartMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    public void testResolveHereDocEndMarkerWithEval() throws Exception {
        PsiReference startMarker = configure();
        Assert.assertTrue(startMarker instanceof BashHereDocStartMarker);
        Assert.assertTrue(startMarker.resolve() instanceof BashHereDocEndMarker);
    }

    protected String getTestDataPath() {
        return getBasePath() + "/psi/resolve/hereDoc/";
    }
}
