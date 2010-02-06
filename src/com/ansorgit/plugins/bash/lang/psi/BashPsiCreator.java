/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiCreator.java, Class: BashPsiCreator
 * Last modified: 2010-02-06
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

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBackquoteImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBlockImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashShebangImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashSymbolImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.arithmetic.*;
import com.ansorgit.plugins.bash.lang.psi.impl.command.*;
import com.ansorgit.plugins.bash.lang.psi.impl.expression.BashRedirectExprImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.expression.BashRedirectListImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.expression.BashSubshellCommandImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.function.BashFunctionDefImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.heredoc.BashHereDocEndMarkerImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.heredoc.BashHereDocImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.heredoc.BashHereDocStartMarkerImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashForImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashSelectImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashUntilImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashWhileImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.shell.*;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashComposedVarImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarDefImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.vars.BashVarSubstitutionImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashExpansionImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashStringImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.word.BashWordImpl;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Static factory class which creates PsiElements for the different token / element types.
 * <p/>
 * Date: 11.04.2009
 * Time: 23:12:51
 *
 * @author Joachim Ansorg
 */
public class BashPsiCreator implements BashElementTypes {
    private static final Logger log = Logger.getInstance("#BashPsiCreator");

    public static PsiElement createElement(ASTNode node) {
        final IElementType elementType = node.getElementType();

        //Bash shebang line
        if (elementType == SHEBANG_ELEMENT) return new BashShebangImpl(node);

        //Block
        if (elementType == BLOCK_ELEMENT) return new BashBlockImpl(node);
        if (elementType == GROUP_COMMAND) return new BashBlockImpl(node);

        //shell command elements
        if (elementType == FUNCTION_DEF_COMMAND) return new BashFunctionDefImpl(node);
        if (elementType == BACKQUOTE_COMMAND) return new BashBackquoteImpl(node);
        if (elementType == SUBSHELL_COMMAND) return new BashSubshellCommandImpl(node);
        if (elementType == PIPELINE_COMMAND) return new BashPipelineImpl(node);
        if (elementType == COMPOSED_COMMAND) return new BashComposedCommandImpl(node);

        //loops
        if (elementType == FOR_COMMAND) return new BashForImpl(node);
        if (elementType == WHILE_COMMAND) return new BashWhileImpl(node);
        if (elementType == SELECT_COMMAND) return new BashSelectImpl(node);
        if (elementType == UNTIL_COMMAND) return new BashUntilImpl(node);

        //other shell things
        if (elementType == IF_COMMAND) return new BashIfImpl(node);
        if (elementType == CONDITIONAL_COMMAND) return new BashConditionalCommandImpl(node);
        if (elementType == CASE_COMMAND) return new BashCaseImpl(node);
        if (elementType == CASE_PATTERN_ELEMENT) return new BashCasePatternImpl(node);
        if (elementType == CASE_PATTERN_LIST_ELEMENT) return new BashCasePatternListElementImpl(node);
        if (elementType == TIME_COMMAND) return new BashTimeCommandImpl(node);
        if (elementType == REDIRECT_ELEMENT) return new BashRedirectExprImpl(node);
        if (elementType == REDIRECT_LIST_ELEMENT) return new BashRedirectListImpl(node);

        //vars
        if (elementType == VAR_DEF_ELEMENT) return new BashVarDefImpl(node);
        if (elementType == VAR_ELEMENT) return new BashVarImpl(node);
        if (elementType == VAR_SUBSTITUTION_ELEMENT) return new BashVarSubstitutionImpl(node);
        if (elementType == VAR_COMPOSED_VAR_ELEMENT) return new BashComposedVarImpl(node);

        //commands
        if (elementType == SIMPLE_COMMAND_ELEMENT) return new BashCommandImpl(node);

        //misc elements
        if (elementType == STRING_ELEMENT) return new BashStringImpl(node);
        if (elementType == HEREDOC_ELEMENT) return new BashHereDocImpl(node);
        if (elementType == SYMBOL_ELEMENT) return new BashSymbolImpl(node);
        if (elementType == PARSED_WORD_ELEMENT) return new BashWordImpl(node);
        if (elementType == EXPANSION_ELEMENT) return new BashExpansionImpl(node);

        if (elementType == ARITHMETIC_COMMAND) return new BashCommandImpl(node);
        if (elementType == INTERNAL_COMMAND_ELEMENT) return new BashInternalCommandImpl(node);
        if (elementType == GENERIC_COMMAND_ELEMENT) return new BashGenericCommandImpl(node);

        if (elementType == HEREDOC_START_MARKER_ELEMENT) return new BashHereDocStartMarkerImpl(node);
        if (elementType == HEREDOC_END_MARKER_ELEMENT) return new BashHereDocEndMarkerImpl(node);

        if (elementType == ARITH_ASSIGNMENT) return new AssignmentExpressionsImpl(node);
        if (elementType == ARITH_MUL) return new ProductExpressionsImpl(node);
        if (elementType == ARITH_SUM) return new SumExpressionsImpl(node);
        if (elementType == ARITH_SIMPLE) return new SimpleExpressionsImpl(node);
        if (elementType == ARITH_PARENS) return new ParenthesesExpressionsImpl(node);

        log.warn("MISSING PSI for" + node);

        return new ASTWrapperPsiElement(node);
    }
}
