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

package com.ansorgit.plugins.bash.editor.refactoring;

import com.ansorgit.plugins.bash.BashCodeInsightFixtureTestCase;
import com.google.common.collect.Lists;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.testFramework.builders.ModuleFixtureBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * This test checks renaming of references which are in files outside of a source root.
 *
 * @author jansorg
 */
public class RenameOutsideSourceRootTest extends BashCodeInsightFixtureTestCase {
    @Override
    protected String getBasePath() {
        return "/editor/refactoring/RenameOutsideSourceRootTest/";
    }

    @Override
    protected void tuneFixture(ModuleFixtureBuilder moduleBuilder) {
        moduleBuilder.addContentRoot(myFixture.getTempDirPath());
        moduleBuilder.addSourceContentRoot("src");
    }

    @Test
    public void testVariableRename() throws Exception {
        doRename("source.bash", "included.bash");
    }

    @Test
    public void testFunctionRename() throws Exception {
        doRename("source.bash", "included.bash");
    }

    @Test
    public void testFileRename() throws Exception {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));
        myFixture.configureByFiles("source.bash", "included.bash");

        myFixture.renameElementAtCaret("included_renamed.bash");

        myFixture.checkResultByFile("source.bash", "source_after.bash", false);
        Assert.assertNotNull(myFixture.findFileInTempDir("included_renamed.bash")); //make sure the renamed file exists
    }

    private void doRename(String... sourceFiles) {
        myFixture.setTestDataPath(getTestDataPath() + getTestName(true));

        List<String> filenames = Lists.newArrayList(sourceFiles);
        myFixture.configureByFiles(filenames.toArray(new String[filenames.size()]));

        myFixture.renameElementAtCaret("a_renamed");

        for (String filename : filenames) {
            myFixture.checkResultByFile(filename, FileUtil.getNameWithoutExtension(filename) + "_after." + FileUtilRt.getExtension(filename), false);
        }
    }
}
