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

package com.ansorgit.plugins.bash.lang.psi.fileInclude;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.intellij.psi.PsiReference;
import com.intellij.usageView.UsageInfo;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

public class FunctionFindUsgesTestCase extends LightBashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "psi/usageSearch/function/";
    }

    @Test
    public void testFindUsages1() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"source.bash", "includedFile.bash"});
    }

    @Test
    public void testFindUsages2() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"includedFile.bash", "source.bash"});

    }

    @Test
    public void testFindUsages3() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        assertUsages(new String[]{"subdir/includedFile.bash", "subdir/source.bash"});
    }

    private void assertUsages(String[] files) {
        PsiReference reference = myFixture.getReferenceAtCaretPosition(files);
        Assert.assertNotNull("Unable to find the reference", reference);
        Assert.assertNotNull("Unable to resolve reference", reference.resolve());

        Collection<UsageInfo> usages = myFixture.findUsages(reference.resolve());
        Assert.assertEquals("Not all usages were found (calls in includedFile.bash and source.bash)", 2, usages.size());
    }
}
