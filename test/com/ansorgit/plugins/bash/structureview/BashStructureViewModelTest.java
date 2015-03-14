/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashStructureViewModelTest.java, Class: BashStructureViewModelTest
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

package com.ansorgit.plugins.bash.structureview;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.newStructureView.StructureViewComponent;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.StructureViewTestCase;
import junit.framework.Assert;

/**
 * User: jansorg
 * Date: 17.07.2010
 * Time: 10:32:04
 */
public class BashStructureViewModelTest extends StructureViewTestCase {
    public void testStructureView() throws Exception {
        VirtualFile file = configure();
        Assert.assertNotNull(file);

        Test test = new Test() {
            public void test(StructureViewComponent component) {
                StructureViewModel tree = component.getTreeModel();
                Assert.assertNotNull(tree);

                StructureViewTreeElement root = tree.getRoot();
                Assert.assertNotNull(root);

                TreeElement[] children = root.getChildren();
                Assert.assertEquals(1, children.length);

                TreeElement firstChild = children[0];
                Assert.assertNotNull(firstChild);

                Assert.assertEquals("a()", firstChild.getPresentation().getPresentableText());

                Assert.assertNotNull(firstChild.getChildren());
                Assert.assertEquals(0, firstChild.getChildren().length);
            }
        };

        doTest(test);
    }

    protected VirtualFile configure() throws Exception {
        return configureByFile(getTestName(false) + ".bash", null);
    }

    protected String getTestDataPath() {
        return BashTestUtils.getBasePath() + "/structureview/";
    }
}
