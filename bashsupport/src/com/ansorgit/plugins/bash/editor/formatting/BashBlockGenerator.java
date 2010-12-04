/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashBlockGenerator.java, Class: BashBlockGenerator
 * Last modified: 2010-05-13
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

import com.ansorgit.plugins.bash.editor.formatting.processor.BashIndentProcessor;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.psi.codeStyle.CodeStyleSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate myBlock hierarchy
 * <p/>
 * This code was taken from the Groovy plugin.
 *
 * @author ilyas, jansorg
 */
public class BashBlockGenerator implements BashElementTypes {

    private static Alignment myAlignment;
    private static Wrap myWrap;
    private static CodeStyleSettings mySettings;

    public static List<Block> generateSubBlocks(ASTNode node,
                                                Alignment _myAlignment,
                                                Wrap _myWrap,
                                                CodeStyleSettings _mySettings,
                                                BashBlock block) {
        myWrap = _myWrap;
        mySettings = _mySettings;
        myAlignment = _myAlignment;

        // For other cases
        final List<Block> subBlocks = new ArrayList<Block>();
        ASTNode children[] = getBashChildren(node);
        ASTNode prevChildNode = null;
        for (ASTNode childNode : children) {
            if (canBeCorrectBlock(childNode)) {
                final Indent indent = BashIndentProcessor.getChildIndent(block, prevChildNode, childNode);
                subBlocks.add(new BashBlock(childNode, myAlignment, indent, myWrap, mySettings));
                prevChildNode = childNode;
            }
        }
        return subBlocks;
    }


    /**
     * @param node Tree node
     * @return true, if the current node can be myBlock node, else otherwise
     */
    private static boolean canBeCorrectBlock(final ASTNode node) {
        return (node.getText().trim().length() > 0);
    }

    private static ASTNode[] getBashChildren(final ASTNode node) {
        return node.getChildren(null);
    }

}
