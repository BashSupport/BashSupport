/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFoldingBuilder.java, Class: BashFoldingBuilder
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

package com.ansorgit.plugins.bash.editor.codefolding;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Code folding builder for the Bash language.
 *
 * @author Joachim Ansorg, mail@joachim-ansorg.de
 */
public class BashFoldingBuilder implements FoldingBuilder, BashElementTypes {
    @NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        appendDescriptors(node, document, descriptors);

        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private static ASTNode appendDescriptors(final ASTNode node, final Document document, final List<FoldingDescriptor> descriptors) {
        final IElementType type = node.getElementType();

        if (isFoldable(type)) {
            int startLine = document.getLineNumber(node.getStartOffset());

            TextRange adjustedFoldingRange = adjustFoldingRange(node);
            int endLine = document.getLineNumber(adjustedFoldingRange.getEndOffset());

            if (startLine + minumumLineOffset(type) <= endLine) {
                descriptors.add(new FoldingDescriptor(node, adjustedFoldingRange));
            }
        }

        if (mayContainFoldBlocks(type)) {
            //work on all child elements
            ASTNode child = node.getFirstChildNode();
            while (child != null) {
                child = appendDescriptors(child, document, descriptors).getTreeNext();
            }
        }

        return node;
    }

    private static TextRange adjustFoldingRange(ASTNode node) {
        if (node.getElementType() == HEREDOC_ELEMENT) {
            TextRange textRange = node.getTextRange();
            return TextRange.from(textRange.getStartOffset(), textRange.getLength() - 1);
        }

        return node.getTextRange();
    }

    private static boolean mayContainFoldBlocks(IElementType type) {
        return type != HEREDOC_ELEMENT;
    }

    private static boolean isFoldable(IElementType type) {
        return type == BLOCK_ELEMENT
                || type == GROUP_COMMAND
                || type == CASE_PATTERN_LIST_ELEMENT
                || type == HEREDOC_ELEMENT;
    }

    private static int minumumLineOffset(IElementType type) {
        return 2;
    }


    public String getPlaceholderText(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();
        if (!isFoldable(type)) {
            return null;
        }

        if (type == HEREDOC_ELEMENT) {
            return "...";
        }

        return "{...}";
    }

    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}
