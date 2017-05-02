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

package com.ansorgit.plugins.bash.errorFiles;

import com.ansorgit.plugins.bash.LightBashCodeInsightFixtureTestCase;
import com.google.common.collect.Lists;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Tests deep nesting during parsing.
 *
 * @author jansorg
 */
public class DeepRecursionParsingTest extends LightBashCodeInsightFixtureTestCase {
    @Test
    public void testIssue310() throws Exception {
        checkFileNesting("310-docker-entrypoint.sh");
    }

    @Test
    public void testIssue310Two() throws Exception {
        checkFileNesting("issue_310_airgeddon.bash");
    }

    private void checkFileNesting(String filePath) {
        PsiFile file = myFixture.configureByFile(filePath);

        final List<PsiErrorElement> errors = Lists.newArrayList();
        file.acceptChildren(new PsiElementVisitor() {
            @Override
            public void visitErrorElement(PsiErrorElement element) {
                if (element.getErrorDescription().startsWith("Internal parser error")) {
                    errors.add(element);
                }
            }
        });

        Assert.assertEquals("Deep nesting must not trigger the recursion guard", 0, errors.size());
    }

    protected String getBasePath() {
        return "errorFiles/";
    }
}
