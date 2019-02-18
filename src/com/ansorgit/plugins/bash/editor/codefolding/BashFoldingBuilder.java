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

package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.*;

/**
 * Code folding builder for the Bash language.
 *
 * @author jansorg
 */
public class BashFoldingBuilder implements FoldingBuilder, BashElementTypes {

    private static final TokenSet foldableTokens = TokenSet.create(GROUP_COMMAND, CASE_PATTERN_LIST_ELEMENT, GROUP_ELEMENT, LOGICAL_BLOCK_ELEMENT, SUBSHELL_COMMAND);
    private static final TokenSet startLogicalBlockTokens = TokenSet.create(THEN_KEYWORD, DO_KEYWORD);
    private static final TokenSet endLogicalBlockTokens = TokenSet.create(FI_KEYWORD, DONE_KEYWORD);

    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = Lists.newArrayList();

        appendDescriptors(node, document, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();

        if (!isFoldable(node)) {
            return null;
        }

        if (type == HEREDOC_CONTENT_ELEMENT) {
            return "...";
        }

        if (type == SUBSHELL_COMMAND) {
            return "(...)";
        }

        if (isFoldableBlock(node, THEN_KEYWORD)) {
            return "then...fi";
        }

        if (isFoldableBlock(node, DO_KEYWORD)) {
            return "do...done";
        }

        return "{...}";
    }

    private boolean isFoldableBlock(ASTNode node, IElementType keyword) {
        if (node.getElementType() == LOGICAL_BLOCK_ELEMENT) {
            final PsiElement prev = PsiTreeUtil.prevVisibleLeaf(node.getPsi());
            return prev != null
                    && prev.getNode().getElementType() == keyword;
        }
        return false;
    }

    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }

    private static ASTNode appendDescriptors(final ASTNode node, final Document document, final List<FoldingDescriptor> descriptors) {
        if (isFoldable(node)) {
            final IElementType type = node.getElementType();

            int startLine = document.getLineNumber(node.getStartOffset());

            TextRange adjustedFoldingRange = adjustFoldingRange(node);
            int endLine = document.getLineNumber(adjustedFoldingRange.getEndOffset());

            if (startLine + minumumLineOffset(type) <= endLine) {
                descriptors.add(new FoldingDescriptor(node, adjustedFoldingRange));
            }
        }

        if (mayContainFoldBlocks(node)) {
            //work on all child elements
            ASTNode child = node.getFirstChildNode();
            while (child != null) {
                child = appendDescriptors(child, document, descriptors).getTreeNext();
            }
        }

        return node;
    }

    private static TextRange adjustFoldingRange(ASTNode node) {
        if (node.getElementType() == HEREDOC_CONTENT_ELEMENT) {
            int startOffset = node.getStartOffset();

            //walk to the last content element before the close marker
            for (ASTNode next = node.getTreeNext(); next != null; next = next.getTreeNext()) {
                IElementType elementType = next.getElementType();
                if (elementType == BashElementTypes.HEREDOC_END_ELEMENT || elementType == BashElementTypes.HEREDOC_END_IGNORING_TABS_ELEMENT) {
                    return TextRange.create(startOffset, next.getStartOffset() - 1);
                }
            }
        } else if (node.getElementType() == LOGICAL_BLOCK_ELEMENT) {

            //walk to the last content element before the close marker
            int startOffset = getStartOffset(node);

            //walk to the last content element before the close marker
            for (ASTNode next = node.getTreeNext(); next != null; next = next.getTreeNext()) {
                IElementType elementType = next.getElementType();
                if (endLogicalBlockTokens.contains(elementType)) {
                    return TextRange.create(startOffset, next.getTextRange().getEndOffset());
                }
            }

        }

        return node.getTextRange();
    }

    private static int getStartOffset(ASTNode node) {
        for (ASTNode prev = node.getTreePrev(); prev != null; prev = prev.getTreePrev()) {
            IElementType elementType = prev.getElementType();
            if (startLogicalBlockTokens.contains(elementType)) {
                return prev.getStartOffset();
            }
        }
        return node.getStartOffset();
    }

    private static boolean mayContainFoldBlocks(ASTNode node) {
        IElementType type = node.getElementType();

        return type != HEREDOC_CONTENT_ELEMENT;
    }

    private static boolean isFoldable(ASTNode node) {
        IElementType type = node.getElementType();

        if (type == HEREDOC_CONTENT_ELEMENT) {
            // only the first heredoc element of a single heredoc is foldable,
            // the range expansion expands it until the last content element
            ASTNode prev = node.getTreePrev();
            if (prev != null && prev.getElementType() == BashTokenTypes.LINE_FEED) {
                //first heredoc content element, better PSI would be an improvement here
                return true;
            }
        }

        return foldableTokens.contains(type);
    }

    private static int minumumLineOffset(IElementType type) {
        return 2;
    }
}
