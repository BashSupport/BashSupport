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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.lexer.BashElementType;
import com.ansorgit.plugins.bash.lang.parser.eval.BashEvalElementType;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashIncludeCommand;
import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBackquoteImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashGroupImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.BashLogicalBlockImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.expression.BashSubshellCommandImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashForImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashSelectImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashUntilImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.loops.BashWhileImpl;
import com.ansorgit.plugins.bash.lang.psi.impl.shell.*;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.*;
import com.ansorgit.plugins.bash.lang.psi.stubs.elements.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.tree.ICompositeElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.util.ReflectionUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

/**
 * The available Bash parser element types.
 *
 * @author jansorg
 */
public interface BashElementTypes {
    IStubFileElementType FILE = new BashStubFileElementType();
    IElementType FILE_REFERENCE = new BashElementType("File reference");
    IElementType SHEBANG_ELEMENT = new BashElementType("shebang element");
    IElementType GROUP_ELEMENT = new BashCompositeElementType("group element", BashGroupImpl.class);

    IElementType LOGICAL_BLOCK_ELEMENT = new BashCompositeElementType("logical block", BashLogicalBlockImpl.class);

    //Var usage
    IStubElementType<BashVarStub, BashVar> VAR_ELEMENT = new BashVarElementType();

    IElementType VAR_COMPOSED_VAR_ELEMENT = new BashElementType("composed variable, like subshell");
    IElementType PARSED_WORD_ELEMENT = new BashElementType("combined word");
    IElementType PARAM_EXPANSION_ELEMENT = new BashElementType("var substitution");
    IElementType FUNCTION_DEF_NAME_ELEMENT = new BashElementType("named symbol");
    //redirect elements
    IElementType REDIRECT_LIST_ELEMENT = new BashElementType("redirect list");
    IElementType REDIRECT_ELEMENT = new BashElementType("redirect element");
    IElementType PROCESS_SUBSTITUTION_ELEMENT = new BashElementType("process substitution element");
    //command elements
    IStubElementType<BashCommandStub, BashCommand> SIMPLE_COMMAND_ELEMENT = new BashSimpleCommandElementType();
    IStubElementType<BashVarDefStub, BashVarDef> VAR_DEF_ELEMENT = new BashVarDefElementType();
    IElementType GENERIC_COMMAND_ELEMENT = new BashElementType("generic bash command");
    IStubElementType<BashIncludeCommandStub, BashIncludeCommand> INCLUDE_COMMAND_ELEMENT = new BashIncludeCommandElementType();
    //pipeline commands
    IElementType PIPELINE_COMMAND = new BashElementType("pipeline command");
    //composed command, i.e. a && b
    IElementType COMPOSED_COMMAND = new BashElementType("composed command");
    //shell commands
    IElementType WHILE_COMMAND = new BashCompositeElementType("while loop", BashWhileImpl.class);
    IElementType UNTIL_COMMAND = new BashCompositeElementType("until loop", BashUntilImpl.class);
    IElementType FOR_COMMAND = new BashCompositeElementType("for shellcommand", BashForImpl.class);
    IElementType SELECT_COMMAND = new BashCompositeElementType("select command", BashSelectImpl.class);
    IElementType IF_COMMAND = new BashCompositeElementType("if shellcommand", BashIfImpl.class);
    IElementType CONDITIONAL_COMMAND = new BashCompositeElementType("conditional shellcommand", BashConditionalCommandImpl.class);
    IElementType EXTENDED_CONDITIONAL_COMMAND = new BashCompositeElementType("extended conditional shellcommand", BashExtendedConditionalCommandImpl.class);
    IElementType SUBSHELL_COMMAND = new BashCompositeElementType("subshell shellcommand", BashSubshellCommandImpl.class);
    IElementType BACKQUOTE_COMMAND = new BashCompositeElementType("backquote shellcommand", BashBackquoteImpl.class);
    IElementType TRAP_COMMAND = new BashCompositeElementType("trap command", BashTrapCommandImpl.class);
    IElementType LET_COMMAND = new BashCompositeElementType("let command", BashLetCommandImpl.class);
    IStubElementType<BashFunctionDefStub, BashFunctionDef> FUNCTION_DEF_COMMAND = new BashFunctionDefElementType();
    IElementType GROUP_COMMAND = new BashCompositeElementType("group command", BashGroupImpl.class);
    //arithmetic commands
    IElementType ARITHMETIC_COMMAND = new BashElementType("arithmetic command");
    IElementType ARITH_ASSIGNMENT_CHAIN_ELEMENT = new BashElementType("arithmetic assignment chain");
    IElementType ARITH_ASSIGNMENT_ELEMENT = new BashElementType("arithmetic assignment");
    IElementType ARITH_VARIABLE_OPERATOR_ELEMENT = new BashElementType("arithmetic with variable operator");
    IElementType ARITH_SUM_ELEMENT = new BashElementType("arithmetic sum");
    IElementType ARITH_BIT_OR_ELEMENT = new BashElementType("arithmetic bitwise or");
    IElementType ARITH_BIT_XOR_ELEMENT = new BashElementType("arithmetic bitwise xor");
    IElementType ARITH_BIT_AND_ELEMENT = new BashElementType("arithmetic bitwise and");
    IElementType ARITH_COMPUND_COMPARISION_ELEMENT = new BashElementType("arith compund comparision");
    IElementType ARITH_EQUALITY_ELEMENT = new BashElementType("arithmetic equality");
    IElementType ARITH_EXPONENT_ELEMENT = new BashElementType("arithmetic exponent");
    IElementType ARITH_LOGIC_AND_ELEMENT = new BashElementType("arithmetic logic and");
    IElementType ARITH_LOGIC_OR_ELEMENT = new BashElementType("arithmetic logic or");
    IElementType ARITH_MULTIPLICACTION_ELEMENT = new BashElementType("arithmetic multiplication");
    IElementType ARITH_NEGATION_ELEMENT = new BashElementType("arithmetic negation");
    IElementType ARITH_POST_INCR_ELEMENT = new BashElementType("arithmetic post incr");
    IElementType ARITH_PRE_INC_ELEMENT = new BashElementType("arithmetic pre incr");
    IElementType ARITH_SHIFT_ELEMENT = new BashElementType("arithmetic shift");
    IElementType ARITH_SIMPLE_ELEMENT = new BashElementType("arithmetic simple");
    IElementType ARITH_TERNERAY_ELEMENT = new BashElementType("arithmetic ternary operator");
    IElementType ARITH_PARENS_ELEMENT = new BashElementType("arithmetic parenthesis expr");
    IElementType CASE_COMMAND = new BashCompositeElementType("case pattern", BashCaseImpl.class);
    IElementType CASE_PATTERN_LIST_ELEMENT = new BashElementType("case pattern list");
    IElementType CASE_PATTERN_ELEMENT = new BashElementType("case pattern");
    IElementType TIME_COMMAND = new BashCompositeElementType("time with optional -p", BashTimeCommandImpl.class);
    //misc
    IElementType EXPANSION_ELEMENT = new BashElementType("single bash expansion");
    IElementType VAR_ASSIGNMENT_LIST = new BashElementType("array assignment list");
    IElementType STRING_ELEMENT = new BashElementType("string");
    IElementType LET_LAZY_EXPRESSION = new BashElementType("lazy LET expression");
    IElementType HEREDOC_START_ELEMENT = new BashElementType("heredoc start element");
    IElementType HEREDOC_CONTENT_ELEMENT = new BashElementType("heredoc content element");
    IElementType HEREDOC_END_ELEMENT = new BashElementType("heredoc end element");
    IElementType HEREDOC_END_IGNORING_TABS_ELEMENT = new BashElementType("heredoc end element (ignoring tabs)");

    IElementType EVAL_BLOCK = new BashEvalElementType();

    IElementType BINARY_DATA = new BashElementType("binary data");

    class BashCompositeElementType extends IBashElementType implements ICompositeElementType {
        private final Constructor<? extends ASTNode> myConstructor;

        private BashCompositeElementType(@NonNls final String debugName, final Class<? extends ASTNode> nodeClass) {
            this(debugName, nodeClass, false);
        }

        private BashCompositeElementType(@NonNls final String debugName, final Class<? extends ASTNode> nodeClass, final boolean leftBound) {
            super(debugName);
            myConstructor = ReflectionUtil.getDefaultConstructor(nodeClass);
        }

        @NotNull
        @Override
        public ASTNode createCompositeNode() {
            return ReflectionUtil.createInstance(myConstructor);
        }
    }

}
