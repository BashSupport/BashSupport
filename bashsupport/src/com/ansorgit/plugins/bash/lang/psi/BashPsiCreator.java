/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiCreator.java, Class: BashPsiCreator
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

package com.ansorgit.plugins.bash.lang.psi;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.psi.impl.*;
import com.ansorgit.plugins.bash.lang.psi.impl.arithmetic.*;
import com.ansorgit.plugins.bash.lang.psi.impl.command.*;
import com.ansorgit.plugins.bash.lang.psi.impl.expression.BashFiledescriptorImpl;
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
import com.ansorgit.plugins.bash.lang.psi.impl.vars.*;
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
        if (elementType == SHEBANG_ELEMENT) {
            return new BashShebangImpl(node);
        }

        //Block
        if (elementType == BLOCK_ELEMENT) {
            return new BashBlockImpl(node);
        }
        if (elementType == GROUP_COMMAND) {
            return new BashBlockImpl(node);
        }

        //shell command elements
        if (elementType == FUNCTION_DEF_COMMAND) {
            return new BashFunctionDefImpl(node);
        }
        if (elementType == BACKQUOTE_COMMAND) {
            return new BashBackquoteImpl(node);
        }
        if (elementType == SUBSHELL_COMMAND) {
            return new BashSubshellCommandImpl(node);
        }
        if (elementType == PIPELINE_COMMAND) {
            return new BashPipelineImpl(node);
        }
        if (elementType == COMPOSED_COMMAND) {
            return new BashComposedCommandImpl(node);
        }

        //loops
        if (elementType == FOR_COMMAND) {
            return new BashForImpl(node);
        }
        if (elementType == WHILE_COMMAND) {
            return new BashWhileImpl(node);
        }
        if (elementType == SELECT_COMMAND) {
            return new BashSelectImpl(node);
        }
        if (elementType == UNTIL_COMMAND) {
            return new BashUntilImpl(node);
        }

        //other shell things
        if (elementType == IF_COMMAND) {
            return new BashIfImpl(node);
        }
        if (elementType == CONDITIONAL_COMMAND) {
            return new BashConditionalCommandImpl(node);
        }
        if (elementType == CASE_COMMAND) {
            return new BashCaseImpl(node);
        }
        if (elementType == CASE_PATTERN_ELEMENT) {
            return new BashCasePatternImpl(node);
        }
        if (elementType == CASE_PATTERN_LIST_ELEMENT) {
            return new BashCasePatternListElementImpl(node);
        }
        if (elementType == TIME_COMMAND) {
            return new BashTimeCommandImpl(node);
        }
        if (elementType == REDIRECT_ELEMENT) {
            return new BashRedirectExprImpl(node);
        }
        if (elementType == REDIRECT_LIST_ELEMENT) {
            return new BashRedirectListImpl(node);
        }
        if (elementType == BashTokenTypes.FILEDESCRIPTOR) {
            return new BashFiledescriptorImpl(node);
        }
        if (elementType == FILE_REFERENCE) {
            return new BashFileReferenceImpl(node);
        }

        //vars
        if (elementType == VAR_DEF_ELEMENT) {
            return new BashVarDefImpl(node);
        }
        if (elementType == VAR_ELEMENT) {
            return new BashVarImpl(node);
        }
        if (elementType == PARAM_EXPANSION_ELEMENT) {
            return new BashParameterExpansionImpl(node);
        }
        if (elementType == VAR_COMPOSED_VAR_ELEMENT) {
            return new BashComposedVarImpl(node);
        }
        if (elementType == VAR_ASSIGNMENT_LIST) {
            return new BashAssignmentListImpl(node);
        }

        //commands
        if (elementType == SIMPLE_COMMAND_ELEMENT) {
            return new BashCommandImpl(node);
        }
        if (elementType == INCLUDE_COMMAND_ELEMENT) {
            return new BashIncludeCommandImpl(node);
        }

        //misc elements
        if (elementType == STRING_ELEMENT) {
            return new BashStringImpl(node);
        }
        if (elementType == SYMBOL_ELEMENT) {
            return new BashSymbolImpl(node);
        }

        if (elementType == PARSED_WORD_ELEMENT) {
            return new BashWordImpl(node);
        }

        if (elementType == EXPANSION_ELEMENT) {
            return new BashExpansionImpl(node);
        }

        if (elementType == ARITHMETIC_COMMAND) {
            return new BashCommandImpl(node);
        }

        if (elementType == GENERIC_COMMAND_ELEMENT) {
            return new BashGenericCommandImpl(node);
        }

        if (elementType == HEREDOC_START_MARKER_ELEMENT) {
            return new BashHereDocStartMarkerImpl(node);
        }

        if (elementType == HEREDOC_ELEMENT) {
            return new BashHereDocImpl(node);
        }

        if (elementType == HEREDOC_END_MARKER_ELEMENT) {
            return new BashHereDocEndMarkerImpl(node);
        }

        if (elementType == ARITH_ASSIGNMENT_ELEMENT) {
            return new AssignmentExpressionsImpl(node);
        }

        if (elementType == ARITH_BIT_AND_ELEMENT) {
            return new BitwiseAndExpressionsImpl(node);
        }

        if (elementType == ARITH_BIT_OR_ELEMENT) {
            return new BitwiseOrExpressionsImpl(node);
        }

        if (elementType == ARITH_BIT_XOR_ELEMENT) {
            return new BitwiseXorExpressionsImpl(node);
        }

        if (elementType == ARITH_EQUALITY_ELEMENT) {
            return new EqualityExprImpl(node);
        }

        if (elementType == ARITH_LOGIC_AND_ELEMENT) {
            return new LogicalAndImpl(node);
        }

        if (elementType == ARITH_LOGIC_OR_ELEMENT) {
            return new LogicalOrmpl(node);
        }

        if (elementType == ARITH_COMPUND_COMPARISION_ELEMENT) {
            return new CompoundComparisionExpressionsImpl(node);
        }

        if (elementType == ARITH_EXPONENT_ELEMENT) {
            return new ExponentExprImpl(node);
        }

        if (elementType == ARITH_NEGATION_ELEMENT) {
            return new NegationExpressionImpl(node);
        }

        if (elementType == ARITH_PARENS_ELEMENT) {
            return new ParenthesesExpressionsImpl(node);
        }

        if (elementType == ARITH_POST_INCR_ELEMENT) {
            return new PostIncrementExpressionsImpl(node);
        }

        if (elementType == ARITH_PRE_INC_ELEMENT) {
            return new PreIncrementExpressionsImpl(node);
        }

        if (elementType == ARITH_MULTIPLICACTION_ELEMENT) {
            return new ProductExpressionsImpl(node);
        }

        if (elementType == ARITH_SHIFT_ELEMENT) {
            return new ShiftExpressionsImpl(node);
        }

        if (elementType == ARITH_SIMPLE_ELEMENT) {
            return new SimpleExpressionsImpl(node);
        }

        if (elementType == ARITH_SUM_ELEMENT) {
            return new SumExpressionsImpl(node);
        }

        if (elementType == ARITH_TERNERAY_ELEMENT) {
            return new TernaryExpressionsImpl(node);
        }

        if (elementType == ARITH_ASSIGNMENT_CHAIN_ELEMENT) {
            return new AssignmentChainImpl(node);
        }

        if (elementType == PROCESS_SUBSTITUTION_ELEMENT) {
            return new BashProcessSubstitutionImpl(node);
        }

        log.warn("MISSING PSI for" + node);

        return new ASTWrapperPsiElement(node);
    }
}
