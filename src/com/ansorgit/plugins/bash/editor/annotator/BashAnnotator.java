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

package com.ansorgit.plugins.bash.editor.annotator;

import com.ansorgit.plugins.bash.editor.highlighting.BashSyntaxHighlighter;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.ansorgit.plugins.bash.lang.psi.api.BashFunctionDefName;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.IncrementExpression;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.SimpleExpression;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectExpr;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBinaryDataElement;
import com.ansorgit.plugins.bash.lang.psi.util.BashIdentifierUtil;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.intellij.lang.ASTNode;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The annotator for the the Bash language.
 * It takes care of the advanced syntax highlighting options.
 * <br>
 *
 * @author jansorg
 */
public class BashAnnotator implements Annotator {
    private static final TokenSet noWordHighlightErase = TokenSet.orSet(
            TokenSet.create(BashTokenTypes.STRING2),
            BashTokenTypes.arithLiterals,
            TokenSet.create(BashElementTypes.VAR_ELEMENT));

    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder annotationHolder) {
        if (element instanceof BashHereDoc) {
            annotateHereDoc((BashHereDoc) element, annotationHolder);
        } else if (element instanceof BashHereDocStartMarker) {
            annotateHereDocStart(element, annotationHolder);
        } else if (element instanceof BashHereDocEndMarker) {
            annotateHereDocEnd(element, annotationHolder);
        } else if (element instanceof BashCommand) {
            annotateCommand((BashCommand) element, annotationHolder);
        } else if (element instanceof BashVarDef) {
            annotateVarDef((BashVarDef) element, annotationHolder);
            annotateIdentifier((BashVarDef) element, annotationHolder);
        } else if (element instanceof BashVar) {
            highlightVariable((BashVar) element, annotationHolder);
            annotateIdentifier((BashVar) element, annotationHolder);
        } else if (element instanceof BashWord) {
            annotateWord(element, annotationHolder);
        } else if (element instanceof IncrementExpression) {
            annotateArithmeticIncrement((IncrementExpression) element, annotationHolder);
        } else if (element instanceof BashFunctionDefName) {
            annotateFunctionDef((BashFunctionDefName) element, annotationHolder);
        } else if (element instanceof BashRedirectExpr) {
            annotateRedirectExpression((BashRedirectExpr) element, annotationHolder);
        } else if (element instanceof BashBinaryDataElement) {
            annotateBinaryData((BashBinaryDataElement) element, annotationHolder);
        }

        highlightKeywordTokens(element, annotationHolder);
    }

    private void highlightKeywordTokens(@NotNull PsiElement element, @NotNull AnnotationHolder annotationHolder) {
        ASTNode node = element.getNode();
        if (node == null) {
            return;
        }

        IElementType elementType = node.getElementType();
        boolean isKeyword = elementType == BashTokenTypes.IN_KEYWORD_REMAPPED
                || elementType == BashTokenTypes.WORD && "!".equals(element.getText());

        if (isKeyword) {
            Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.KEYWORD);
        }
    }

    private void annotateBinaryData(BashBinaryDataElement element, AnnotationHolder annotationHolder) {
        Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);

        annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.BINARY_DATA);
        annotation.setNeedsUpdateOnTyping(false);
    }

    protected void highlightVariable(@NotNull BashVar element, @NotNull AnnotationHolder annotationHolder) {
        if (element.isBuiltinVar()) {
            //highlighting for built-in variables
            Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.VAR_USE_BUILTIN);
        } else if (element.isParameterExpansion()) {
            //highlighting for composed variables
            Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.VAR_USE_COMPOSED);
        }
    }

    private void annotateRedirectExpression(BashRedirectExpr element, AnnotationHolder annotationHolder) {
        Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.REDIRECTION);
    }

    private void annotateFunctionDef(BashFunctionDefName functionName, AnnotationHolder annotationHolder) {
        Annotation annotation = annotationHolder.createInfoAnnotation(functionName, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.FUNCTION_DEF_NAME);
    }

    private void annotateArithmeticIncrement(IncrementExpression element, AnnotationHolder annotationHolder) {
        List<ArithmeticExpression> subexpressions = element.subexpressions();
        if (subexpressions.isEmpty()) {
            return;
        }

        ArithmeticExpression first = subexpressions.get(0);
        if (first instanceof SimpleExpression && !(first.getFirstChild() instanceof BashVar)) {
            PsiElement operator = element.findOperatorElement();
            if (operator != null) {
                annotationHolder.createErrorAnnotation(operator, "This operator only works on a variable and not on a value.");
            }
        }
    }

    private void annotateWord(PsiElement bashWord, AnnotationHolder annotationHolder) {
        //we have to mark the remapped tokens (which are words now) to have the default word formatting.
        PsiElement child = bashWord.getFirstChild();

        while (child != null && false) {
            if (!noWordHighlightErase.contains(child.getNode().getElementType())) {
                Annotation annotation = annotationHolder.createInfoAnnotation(child, null);
                annotation.setEnforcedTextAttributes(TextAttributes.ERASE_MARKER);

                annotation = annotationHolder.createInfoAnnotation(child, null);
                annotation.setEnforcedTextAttributes(EditorColorsManager.getInstance().getGlobalScheme().getAttributes(HighlighterColors.TEXT));
            }

            child = child.getNextSibling();
        }
    }

    private void highlightVariables(PsiElement containerElement, final AnnotationHolder holder) {
        new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(PsiElement element) {
                if (element instanceof BashVar) {
                    Annotation infoAnnotation = holder.createInfoAnnotation(element, null);
                    infoAnnotation.setTextAttributes(BashSyntaxHighlighter.VAR_USE);
                }

                super.visitElement(element);
            }
        }.visitElement(containerElement);
    }

    private void annotateCommand(BashCommand bashCommand, AnnotationHolder annotationHolder) {
        PsiElement cmdElement = null;
        TextAttributesKey attributesKey = null;

        //if the command consists of a single variable then it should not be highlighted
        //otherwise the variable would be shown without its own variable highlighting
        if (BashPsiUtils.isSingleChildParent(bashCommand, BashTokenTypes.VARIABLE)) {
            return;
        }

        if (BashPsiUtils.isSingleChildParent(bashCommand, BashString.class)) {
            return;
        }

        if (BashPsiUtils.isSingleChildParent(bashCommand, BashBackquote.class)) {
            return;
        }

        if (BashPsiUtils.isSingleChildParent(bashCommand, BashSubshellCommand.class)) {
            return;
        }

        if (bashCommand.isFunctionCall()) {
            cmdElement = bashCommand.commandElement();
            attributesKey = BashSyntaxHighlighter.FUNCTION_CALL;
        } else if (bashCommand.isExternalCommand()) {
            cmdElement = bashCommand.commandElement();
            attributesKey = BashSyntaxHighlighter.EXTERNAL_COMMAND;
        } else if (bashCommand.isInternalCommand()) {
            cmdElement = bashCommand.commandElement();
            attributesKey = BashSyntaxHighlighter.INTERNAL_COMMAND;
        }

        if (cmdElement != null && attributesKey != null) {
            final Annotation annotation = annotationHolder.createInfoAnnotation(cmdElement, null);
            annotation.setTextAttributes(attributesKey);
        }
    }

    private void annotateHereDoc(BashHereDoc element, AnnotationHolder annotationHolder) {
        final Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.HERE_DOC);
        annotation.setNeedsUpdateOnTyping(false);

        if (element.isEvaluatingVariables()) {
            highlightVariables(element, annotationHolder);
        }
    }

    private void annotateHereDocStart(PsiElement element, AnnotationHolder annotationHolder) {
        final Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.HERE_DOC_START);
    }

    private void annotateHereDocEnd(PsiElement element, AnnotationHolder annotationHolder) {
        final Annotation annotation = annotationHolder.createInfoAnnotation(element, null);
        annotation.setTextAttributes(BashSyntaxHighlighter.HERE_DOC_END);
    }

    private void annotateVarDef(BashVarDef bashVarDef, AnnotationHolder annotationHolder) {
        final PsiElement identifier = bashVarDef.findAssignmentWord();
        if (identifier != null) {
            final Annotation annotation = annotationHolder.createInfoAnnotation(identifier, null);
            annotation.setTextAttributes(BashSyntaxHighlighter.VAR_DEF);
        }
    }

    /**
     * Annotates invalid identifiers.
     *
     * @param var              The variable or variable definition to check
     * @param annotationHolder Holder of the annotations
     */
    private void annotateIdentifier(BashVar var, AnnotationHolder annotationHolder) {
        String varName = var.getReferenceName();

        if (!BashIdentifierUtil.isValidVariableName(varName) && !BashPsiUtils.isInEvalBlock(var)) {
            annotationHolder.createErrorAnnotation(var.getReference().getRangeInElement().shiftRight(var.getTextOffset()), String.format("'%s': not a valid identifier", varName));
        }
    }
}
