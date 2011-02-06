/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractResolveTest.java, Class: AbstractResolveTest
 * Last modified: 2010-07-17
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

package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.lang.psi.resolve.AbstractResolveTest;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.junit.Assert;

/**
 * User: jansorg
 * Date: 15.06.2010
 * Time: 21:09:35
 */
public abstract class AbstractFileIncludeTest extends AbstractResolveTest {
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/fileInclude/";
    }

    protected PsiElement checkWithIncludeFile(String fileName, boolean assertDefInInclude) throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile(fileName);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNotNull("Variable is not properly resolved", def);

        boolean defIsInIncludeFile = def.getContainingFile().equals(includeFile);
        if (assertDefInInclude) {
            Assert.assertTrue("The variable is not defined in the include file.", defIsInIncludeFile);
        } else {
            Assert.assertFalse("The variable must not be defined in the include file.", defIsInIncludeFile);
        }

        return def;
    }

    protected void assertUnresolved(String includeFilePath) throws Exception {
        PsiReference reference = configure();
        Assert.assertNotNull(reference);

        PsiFile includeFile = addFile(includeFilePath);
        Assert.assertNotNull(includeFile);

        //the var has to resolve to the definition in the included file
        PsiElement def = reference.resolve();
        Assert.assertNull("Variable must not be resolved", def);
    }
}
