/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSpacingProcessor.java, Class: BashSpacingProcessor
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.editor.formatting.processor;

import com.ansorgit.plugins.bash.editor.formatting.BashBlock;
import com.ansorgit.plugins.bash.editor.formatting.SpacingUtil;
import com.ansorgit.plugins.bash.lang.psi.BashVisitor;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.CompositeElement;

/**
 * This code is based on code taken from the Groovy plugin.
 *
 * @author ilyas, jansorg
 */
public class BashSpacingProcessor extends BashVisitor {
    private static final ThreadLocal<BashSpacingProcessor> mySharedProcessorAllocator = new ThreadLocal<BashSpacingProcessor>();
    private static final Logger LOG = Logger.getInstance("#SpacingProcessor");
    private MyBashSpacingVisitor myBashSpacingVisitor;

    public BashSpacingProcessor(MyBashSpacingVisitor myBashSpacingVisitor) {
        this.myBashSpacingVisitor = myBashSpacingVisitor;
    }

    public static Spacing getSpacing(BashBlock child1, BashBlock child2, CodeStyleSettings settings) {
        return getSpacing(child2.getNode(), settings);
    }

    private static Spacing getSpacing(ASTNode node, CodeStyleSettings settings) {
        BashSpacingProcessor spacingProcessor = mySharedProcessorAllocator.get();
        try {
            if (spacingProcessor == null) {
                spacingProcessor = new BashSpacingProcessor(new MyBashSpacingVisitor(node, settings));
                mySharedProcessorAllocator.set(spacingProcessor);
            } else {
                spacingProcessor.myBashSpacingVisitor = new MyBashSpacingVisitor(node, settings);
            }

            spacingProcessor.doInit();
            return spacingProcessor.getResult();
        } catch (Exception e) {
            LOG.error(e);
            return null;
        } finally {
            spacingProcessor.clear();
        }
    }

    private void clear() {
        if (myBashSpacingVisitor != null) {
            myBashSpacingVisitor.clear();
        }
    }

    private Spacing getResult() {
        return myBashSpacingVisitor.getResult();
    }

    private void doInit() {
        myBashSpacingVisitor.doInit();
    }

    public void setVisitor(MyBashSpacingVisitor visitor) {
        myBashSpacingVisitor = visitor;
    }

    private static class MyBashSpacingVisitor extends BashVisitor {
        private Spacing result;
        private CodeStyleSettings mySettings;
        private ASTNode myChild2;
        private ASTNode myChild1;
        private PsiElement myParent;

        public MyBashSpacingVisitor(ASTNode node, CodeStyleSettings settings) {
            mySettings = settings;
            init(node);
        }

        private void init(final ASTNode child) {
            if (child == null) {
                return;
            }
            ASTNode treePrev = child.getTreePrev();
            while (treePrev != null && SpacingUtil.isWhiteSpace(treePrev)) {
                treePrev = treePrev.getTreePrev();
            }

            if (treePrev == null) {
                init(child.getTreeParent());
            } else {
                myChild2 = child;
                myChild1 = treePrev;
                final CompositeElement parent = (CompositeElement) treePrev.getTreeParent();
                myParent = SourceTreeToPsiMap.treeElementToPsi(parent);
            }
        }

        public void clear() {
        }

        public Spacing getResult() {
            return result;
        }

        public void doInit() {
        }
    }
}

