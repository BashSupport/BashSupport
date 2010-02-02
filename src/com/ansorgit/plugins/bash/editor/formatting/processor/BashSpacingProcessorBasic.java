/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashSpacingProcessorBasic.java, Class: BashSpacingProcessorBasic
 * Last modified: 2010-01-31
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
import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocMarker;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashCase;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashIf;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashStringImpl;
import com.intellij.formatting.Spacing;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public abstract class BashSpacingProcessorBasic implements BashElementTypes, BashTokenTypes {
    private static final Logger log = Logger.getInstance("SpacingProcessorBasic");
    private static TokenSet commandSet = TokenSet.create(GENERIC_COMMAND_ELEMENT, INTERNAL_COMMAND_ELEMENT, SIMPLE_COMMAND_ELEMENT);

    private static final Spacing NO_SPACING_WITH_NEWLINE = Spacing.createSpacing(0, 0, 0, true, 1);
    private static final Spacing NO_SPACING = Spacing.createSpacing(0, 0, 0, false, 0);
    private static final Spacing COMMON_SPACING = Spacing.createSpacing(1, 1, 0, true, 100);
    private static final Spacing COMMON_SPACING_WITH_NL = Spacing.createSpacing(1, 1, 1, true, 100);
    private static final Spacing IMPORT_BETWEEN_SPACING = Spacing.createSpacing(0, 0, 1, true, 100);
    private static final Spacing IMPORT_OTHER_SPACING = Spacing.createSpacing(0, 0, 2, true, 100);
    private static final Spacing LAZY_SPACING = Spacing.createSpacing(0, 239, 0, true, 100);

    public static Spacing getSpacing(BashBlock child1, BashBlock child2, CodeStyleSettings settings) {
        ASTNode leftNode = child1.getNode();
        ASTNode rightNode = child2.getNode();

        IElementType leftType = leftNode.getElementType();
        IElementType rightType = rightNode.getElementType();

        final PsiElement leftPsi = leftNode.getPsi();
        final PsiElement rightPsi = rightNode.getPsi();

        final IElementType leftParentElement = leftPsi != null && leftPsi.getParent() != null ? leftPsi.getParent().getNode().getElementType() : null;
        final IElementType rightParentElement = rightPsi != null && rightPsi.getParent() != null ? rightPsi.getParent().getNode().getElementType() : null;

        //Braces Placement
        // For multi-line strings
        //if (!child1.getNode().getTextRange().equals(child1.getTextRange()) || !child2.getNode().getTextRange().equals(child2.getTextRange())) {
        //    return NO_SPACING;
        //}

        //for composed strings
        if (isNodeInString(leftNode) && isNodeInString(rightNode)) {
            return NO_SPACING;
        }

        //for heredocs
        if (leftPsi.getParent() instanceof BashHereDoc && rightPsi.getParent() instanceof BashHereDoc) {
            return Spacing.getReadOnlySpacing();
        }

        if (leftType == STRING_BEGIN && rightPsi instanceof BashHereDocMarker) {
            return NO_SPACING;
        }

        if (leftPsi instanceof BashHereDocMarker && rightType == STRING_END) {
            return NO_SPACING;
        }

        if (leftType == LINE_FEED && rightType instanceof BashHereDocEndMarker) {
            return Spacing.getReadOnlySpacing();
        }

        //for composed words
        if (leftPsi.getParent() instanceof BashWord && rightPsi.getParent() instanceof BashWord) {
            return NO_SPACING;
        }

        //for conditional expressions [ -f ... ]
        if (leftType == EXPR_CONDITIONAL || rightType == _EXPR_CONDITIONAL) {
            return NO_SPACING;
        }
        if (leftType == BRACKET_KEYWORD || rightType == _BRACKET_KEYWORD) {
            return NO_SPACING;
        }

        //subshell command
        if (leftType == DOLLAR &&
                (rightType == SUBSHELL_COMMAND
                        || rightType == ARITHMETIC_COMMAND
                        || rightType == VAR_SUBSTITUTION_ELEMENT
                        || rightType == VAR_COMPOSED_VAR_ELEMENT)) { // $(...)
            return NO_SPACING;
        }

        if (leftType == DOLLAR && rightType == LEFT_CURLY && rightParentElement == VAR_COMPOSED_VAR_ELEMENT) {
            return NO_SPACING;
        }

        //{} expressions
        if ((leftType == LEFT_CURLY || rightType == RIGHT_CURLY) &&
                (leftPsi.getParent().getNode().getElementType() == VAR_SUBSTITUTION_ELEMENT ||
                        leftPsi.getParent().getNode().getElementType() == VAR_COMPOSED_VAR_ELEMENT)) {
            return NO_SPACING;
        }

        //for backticks
        if ((leftType == BACKQUOTE || rightType == BACKQUOTE) && leftPsi.getParent() instanceof BashBackquote) {
            return NO_SPACING;
        }

        // For leftPsi parentheses in method declarations
        if (LEFT_PAREN.equals(rightNode.getElementType()) &&
                rightNode.getPsi().getParent().getNode() != null &&
                FUNCTION_DEF_COMMAND == rightPsi.getParent().getNode().getElementType()) {
            return NO_SPACING;
        }

        //rightPsi parenthesis in function definitions
        if (RIGHT_PAREN.equals(rightNode.getElementType()) && FUNCTION_DEF_COMMAND == rightParentElement) {
            return NO_SPACING;
        }

        if (FUNCTION_DEF_COMMAND == leftType) {
            return Spacing.createSpacing(0, 0, settings.BLANK_LINES_AROUND_METHOD + 1, settings.KEEP_LINE_BREAKS, 100);
        }

        //if statement
        if (leftType == IF_COMMAND) {
            return COMMON_SPACING_WITH_NL;
        }

        if (leftType == THEN_KEYWORD && leftNode.getPsi().getParent() instanceof BashIf) {
            log.debug("Formatting if-then-else: then");
            return COMMON_SPACING_WITH_NL;
        }

        if (rightType == ELIF_KEYWORD && rightNode.getPsi().getParent() instanceof BashIf) {
            log.debug("Formatting if-then: else");
            return COMMON_SPACING_WITH_NL;
        }

        if (((leftType == ELSE_KEYWORD) && (leftNode.getPsi().getParent() instanceof BashIf)) ||
                ((rightType == ELSE_KEYWORD) && (rightNode.getPsi().getParent() instanceof BashIf))) {
            log.debug("Formatting if-then: else");
            return COMMON_SPACING_WITH_NL;
        }

        if (leftType == FI_KEYWORD && leftNode.getPsi().getParent() instanceof BashIf ||
                rightType == FI_KEYWORD && rightNode.getPsi().getParent() instanceof BashIf) {
            log.debug("Formatting if-then: fi");
            return COMMON_SPACING_WITH_NL;
        }

        //var assignments
        if (leftType == ASSIGNMENT_WORD && rightType == EQ) {
            return NO_SPACING;
        }

        if (leftType == EQ && leftNode.getTreePrev() != null && leftNode.getTreePrev().getElementType() == ASSIGNMENT_WORD) {
            return NO_SPACING;
        }

        //generic commands and function calls
        if ((leftPsi instanceof BashCommand || leftPsi instanceof BashBlock) && ";".equals(rightNode.getText())) {
            return NO_SPACING;
        }

        //semicolon after commands,... (needs some better implementation)
        if (rightType == SEMI) {
            return NO_SPACING;
        }

        //case formatting
        if (leftType == CASE_PATTERN_ELEMENT || rightType == CASE_PATTERN_ELEMENT) {
            return NO_SPACING;
        }

        //in keyword
        if (leftType == IN_KEYWORD && leftPsi.getParent() instanceof BashCase) {
            return COMMON_SPACING_WITH_NL;
        }

        //do keyword
        if (leftType == DO_KEYWORD) {
            return COMMON_SPACING_WITH_NL;
        }

        //done keyword
        if (rightType == DONE_KEYWORD) {
            return COMMON_SPACING_WITH_NL;
        }

        //shebang line
        if (leftType == SHEBANG_ELEMENT) {
            return COMMON_SPACING_WITH_NL;
        }

        //consecutive commands
        /*if (leftType == GENERIC_COMMAND_ELEMENT && rightType == GENERIC_COMMAND_ELEMENT &&
                BashPsiUtils.getElementLineNumber(leftPsi) != BashPsiUtils.getElementLineNumber(rightPsi)) {
            return COMMON_SPACING_WITH_NL;
        } */
        if (leftType == SEMI && leftNode.getTreePrev().getPsi() instanceof BashCommand && rightPsi instanceof BashCommand) {
            return COMMON_SPACING_WITH_NL;
        }

        return COMMON_SPACING;
    }

    private static boolean isInCasePattern(ASTNode node) {
        return node.getTreeParent() != null && node.getTreeParent().getElementType() == CASE_PATTERN_ELEMENT;
    }

    /**
     * A node can be embedded in an string. For now, do not reformat those expressions.
     *
     * @param node
     * @return True if the on of the nodes is embedded in a string
     */
    private static boolean isNodeInString(ASTNode node) {
        if (node.getElementType() == STRING_CHAR) {
            return true;
        }

        PsiElement parent = node.getPsi().getParent();
        while (parent != null) {
            if (parent instanceof BashStringImpl) {
                return true;
            }

            parent = parent.getParent();
        }

        return false;
    }
}
