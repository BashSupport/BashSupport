/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVisitor.java, Class: BashVisitor
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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.psi.api.BashBackquote;
import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.lang.psi.api.BashString;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.expression.BashSubshellCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDoc;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocEndMarker;
import com.ansorgit.plugins.bash.lang.psi.api.heredoc.BashHereDocStartMarker;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashExpansion;
import com.ansorgit.plugins.bash.lang.psi.api.word.BashWord;
import com.intellij.psi.PsiElementVisitor;

/**
 * Date: 15.05.2009
 * Time: 14:48:18
 *
 * @author Joachim Ansorg
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

    public void visitShebang(BashShebang shebang) {
        visitElement(shebang);
    }

    public void visitCombinedWord(BashWord shebang) {
        visitElement(shebang);
    }

    public void visitBackquoteCommand(BashBackquote backquote) {
        visitElement(backquote);
    }

    public void visitSubshell(BashSubshellCommand subshellCommand) {
        visitElement(subshellCommand);
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
}
