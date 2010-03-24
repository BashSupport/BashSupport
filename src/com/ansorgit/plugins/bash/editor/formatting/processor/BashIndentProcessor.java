/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashIndentProcessor.java, Class: BashIndentProcessor
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

package com.ansorgit.plugins.bash.editor.formatting.processor;

import com.ansorgit.plugins.bash.editor.formatting.BashBlock;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is based on code taken from the Groovy plugin.
 *
 * @author ilyas, jansorg
 */
public abstract class BashIndentProcessor implements BashElementTypes, BashTokenTypes {
    private static final TokenSet BLOCKS = TokenSet.create(BLOCK_ELEMENT, GROUP_COMMAND, CASE_PATTERN_LIST_ELEMENT);

    /**
     * Calculates indent, based on code style, between parent block and child node
     *
     * @param parent        parent block
     * @param child         child node
     * @param prevChildNode previous child node
     * @return indent
     */
    @NotNull
    public static Indent getChildIndent(@NotNull final BashBlock parent, @Nullable final ASTNode prevChildNode, @NotNull final ASTNode child) {
        ASTNode astNode = parent.getNode();
        final PsiElement psiParent = astNode.getPsi();

        // For Bash file
        if (psiParent instanceof BashFile) {
            return Indent.getNoneIndent();
        }

        if (BLOCKS.contains(astNode.getElementType())) {
            return indentForBlock(psiParent, child);
        }

        return Indent.getNoneIndent();
    }


    /**
     * Indent for common block
     *
     * @param psiBlock
     * @param child
     * @return
     */
    private static Indent indentForBlock(PsiElement psiBlock, ASTNode child) {
        if (LEFT_CURLY.equals(child.getElementType()) || RIGHT_CURLY.equals(child.getElementType())) {
            return Indent.getNoneIndent();
        }

        return Indent.getNormalIndent();
    }
}

