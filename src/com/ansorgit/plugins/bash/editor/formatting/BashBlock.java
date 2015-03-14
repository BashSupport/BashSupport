/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashBlock.java, Class: BashBlock
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.editor.formatting;

import com.ansorgit.plugins.bash.editor.formatting.processor.BashSpacingProcessor;
import com.ansorgit.plugins.bash.editor.formatting.processor.BashSpacingProcessorBasic;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.ILazyParseableElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Block implementation for the Bash formatter.
 * <p/>
 * This class is based on the block code for the Groovy formatter.
 *
 * @author ilyas, jansorg
 */
public class BashBlock implements Block, BashElementTypes {

    final protected ASTNode myNode;
    final protected Alignment myAlignment;
    final protected Indent myIndent;
    final protected Wrap myWrap;
    final protected CodeStyleSettings mySettings;

    protected List<Block> mySubBlocks = null;

    public BashBlock(@NotNull final ASTNode node, @Nullable final Alignment alignment, @NotNull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings) {
        myNode = node;
        myAlignment = alignment;
        myIndent = indent;
        myWrap = wrap;
        mySettings = settings;
    }

    @NotNull
    public ASTNode getNode() {
        return myNode;
    }

    @NotNull
    public CodeStyleSettings getSettings() {
        return mySettings;
    }

    @NotNull
    public TextRange getTextRange() {
        return myNode.getTextRange();
    }

    @NotNull
    public List<Block> getSubBlocks() {
        if (mySubBlocks == null) {
            mySubBlocks = BashBlockGenerator.generateSubBlocks(myNode, myAlignment, myWrap, mySettings, this);
        }
        return mySubBlocks;
    }

    @Nullable
    public Wrap getWrap() {
        return myWrap;
    }

    @Nullable
    public Indent getIndent() {
        return myIndent;
    }

    @Nullable
    public Alignment getAlignment() {
        return myAlignment;
    }

    /**
     * Returns spacing between neighrbour elements
     *
     * @param child1 left element
     * @param child2 right element
     * @return
     */
    @Nullable
    public Spacing getSpacing(Block child1, Block child2) {
        if ((child1 instanceof BashBlock) && (child2 instanceof BashBlock)) {
            Spacing spacing = BashSpacingProcessor.getSpacing(((BashBlock) child1), ((BashBlock) child2), mySettings);
            return spacing != null ? spacing : BashSpacingProcessorBasic.getSpacing(((BashBlock) child1), ((BashBlock) child2), mySettings);
        }
        return null;
    }

    @NotNull
    public ChildAttributes getChildAttributes(final int newChildIndex) {
        return getAttributesByParent();
    }

    private ChildAttributes getAttributesByParent() {
        ASTNode astNode = myNode;
        final PsiElement psiParent = astNode.getPsi();
        if (psiParent instanceof BashFile) {
            return new ChildAttributes(Indent.getNoneIndent(), null);
        }

        if (BLOCK_ELEMENT == astNode.getElementType()) {
            return new ChildAttributes(Indent.getNormalIndent(), null);
        }

        return new ChildAttributes(Indent.getNoneIndent(), null);
    }


    public boolean isIncomplete() {
        return isIncomplete(myNode);
    }

    /**
     * @param node Tree node
     * @return true if node is incomplete
     */
    public boolean isIncomplete(@NotNull final ASTNode node) {
        if (node.getElementType() instanceof ILazyParseableElementType) {
            return false;
        }
        ASTNode lastChild = node.getLastChildNode();
        while (lastChild != null &&
                !(lastChild.getElementType() instanceof ILazyParseableElementType) &&
                (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
            lastChild = lastChild.getTreePrev();
        }
        return lastChild != null && (lastChild.getPsi() instanceof PsiErrorElement || isIncomplete(lastChild));
    }

    public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }
}
