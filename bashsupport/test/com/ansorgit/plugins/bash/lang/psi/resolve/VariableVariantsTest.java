/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: VariableVariantsTest.java, Class: VariableVariantsTest
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
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.resolve.reference.impl.PsiMultiReference;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: jansorg
 * Date: 30.06.2010
 * Time: 21:34:48
 */
public class VariableVariantsTest extends AbstractResolveTest {
    @Test
    public void testVariableVariants() throws Exception {
        checkVariants(2, configure());
    }

    @Test
    public void testVariableVariantsWithFunctions() throws Exception {
        checkVariants(4, configure());
    }

    @Test
    public void testVariableVariantsFromIncludeFile() throws Exception {
        PsiReference psiReference = configure();
        addFile("include.bash");

        checkVariants(4, psiReference);
    }

    private void checkVariants(int expectedVariantsCount, PsiReference psiReference) throws Exception {
        if (psiReference instanceof PsiMultiReference) {
            PsiMultiReference multiRef = (PsiMultiReference) psiReference;

            for (PsiReference reference : multiRef.getReferences()) {
                if (reference instanceof BashVar) {
                    psiReference = reference;
                    break;
                }
            }
        }

        Assert.assertTrue(psiReference instanceof BashVar);

        BashVar variable = (BashVar) psiReference.getElement();
        Assert.assertNotNull(variable);

        Object[] variants = variable.getVariants();

        int foundVariableDefs = 0;
        for (Object v : variants) {
            if (v instanceof BashVarDef || (v instanceof LookupElementBuilder && ((LookupElementBuilder) v).getObject() instanceof BashVarDef)) {
                foundVariableDefs++;
            }
        }

        Assert.assertEquals(expectedVariantsCount, foundVariableDefs);
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/psi/resolve/variableVariants/";
    }
}
