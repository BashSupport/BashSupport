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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.*;
import com.ansorgit.plugins.bash.lang.psi.api.arithmetic.ArithmeticExpression;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashFiledescriptor;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectExpr;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashRedirectList;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.api.shell.BashConditionalCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashComposedVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.ansorgit.plugins.bash.lang.psi.impl.shell.BashExtendedConditionalCommandImpl;
import com.intellij.psi.PsiElementVisitor;

/**
 * @author jansorg
 */
public class BashVisitor extends PsiElementVisitor {
    public void visitFile(BashFile file) {
        visitElement(file);
    }

    public void visitFunctionDef(BashFunctionDef functionDef) {
        visitElement(functionDef);
    }

    public void visitVarDef(BashVarDef varDef) {
        visitElement(varDef);
    }

    public void visitVarUse(BashVar var) {
        visitElement(var);
    }

    public void visitComposedVariable(BashComposedVar composedVar) {
        visitElement(composedVar);
    }

    public void visitShebang(BashShebang shebang) {
        visitElement(shebang);
    }

    public void visitCombinedWord(BashWord word) {
        visitElement(word);
    }

    public void visitBackquoteCommand(BashBackquote backquote) {
        visitElement(backquote);
    }

    public void visitSubshell(BashSubshellCommand subshellCommand) {
        visitElement(subshellCommand);
    }

    public void visitConditional(BashConditionalCommand conditionalCommand) {
        visitElement(conditionalCommand);
    }

    public void visitExtendedConditional(BashExtendedConditionalCommandImpl extendedConditionalCommand) {
        visitElement(extendedConditionalCommand);
    }

    public void visitInternalCommand(BashCommand bashCommand) {
        visitElement(bashCommand);
    }

    public void visitGenericCommand(BashCommand bashCommand) {
        visitElement(bashCommand);
    }

    public void visitExpansion(BashExpansion bashExpansion) {
        visitElement(bashExpansion);
    }

    public void visitIncludeCommand(BashIncludeCommand fileReference) {
        visitElement(fileReference);
    }

    public void visitFileReference(BashFileReference fileReference) {
        visitElement(fileReference);
    }


    /**
     * Visits a bash char sequence. A char sequence is a string which may consist of
     * a start marker, several content elements and an end marker.
     *
     * @param bashString The string which is visited
     */
    public void visitString(BashString bashString) {
        visitElement(bashString);
    }

    public void visitHereDocEndMarker(BashHereDocEndMarker marker) {
        visitElement(marker);
    }

    public void visitHereDocStartMarker(BashHereDocStartMarker marker) {
        visitElement(marker);
    }

    public void visitHereDoc(BashHereDoc doc) {
        visitElement(doc);
    }

    public void visitArithmeticExpression(ArithmeticExpression expression) {
        visitElement(expression);
    }

    public void visitRedirectExpression(BashRedirectExpr redirect) {
        visitElement(redirect);
    }

    public void visitRedirectExpressionList(BashRedirectList redirectList) {
        visitElement(redirectList);
    }

    public void visitFiledescriptor(BashFiledescriptor filedescriptor) {
        visitElement(filedescriptor);
    }
}
