/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashElementTypes.java, Class: BashElementTypes
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.lexer.BashElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;

/**
 * The available Bash parser element types.
 * <p/>
 * Date: 24.03.2009
 * Time: 20:30:25
 *
 * @author Joachim Ansorg
 */
public interface BashElementTypes {
    public final IFileElementType FILE = new IFileElementType(BashFileType.BASH_LANGUAGE);

    public final IElementType SHEBANG_ELEMENT = new BashElementType("shebang element");

    public final IElementType BLOCK_ELEMENT = new BashElementType("block element");

    //Var usage
    //fixme probably unnecessary
    public final IElementType VAR_ELEMENT = new BashElementType("variable");
    public final IElementType VAR_COMPOSED_VAR_ELEMENT = new BashElementType("composed variable, like subshell");
    public final IElementType PARSED_WORD_ELEMENT = new BashElementType("combined word");
    public final IElementType VAR_SUBSTITUTION_ELEMENT = new BashElementType("var substitution");

    //misc
    public final IElementType EVALUATED_STRING_ELEMENT = new BashElementType("evaluated string");
    public final IElementType SYMBOL_ELEMENT = new BashElementType("named symbol");

    //redirect elements
    public final IElementType REDIRECT_LIST_ELEMENT = new BashElementType("redirect list");
    public final IElementType REDIRECT_ELEMENT = new BashElementType("redirect element");

    //command elements
    public final IElementType SIMPLE_COMMAND_ELEMENT = new BashElementType("simple command element");
    public final IElementType VAR_DEF_ELEMENT = new BashElementType("assignment command element");
    public final IElementType INTERNAL_COMMAND_ELEMENT = new BashElementType("internal bash command");
    public final IElementType GENERIC_COMMAND_ELEMENT = new BashElementType("generic bash command");

    //pipeline commands
    public final IElementType PIPELINE_COMMAND = new BashElementType("pipeline command");

    //composed command, i.e. a && b
    public final IElementType COMPOSED_COMMAND = new BashElementType("composed command");

    //shell commands
    public final IElementType WHILE_COMMAND = new BashElementType("while loop");
    public final IElementType UNTIL_COMMAND = new BashElementType("until loop");
    public final IElementType FOR_COMMAND = new BashElementType("for shellcommand");
    public final IElementType SELECT_COMMAND = new BashElementType("select command");
    public final IElementType IF_COMMAND = new BashElementType("if shellcommand");
    public final IElementType CONDITIONAL_COMMAND = new BashElementType("conditional shellcommand");
    public final IElementType SUBSHELL_COMMAND = new BashElementType("subshell shellcommand");
    public final IElementType BACKQUOTE_COMMAND = new BashElementType("backquote shellcommand");
    public final IElementType FUNCTION_DEF_COMMAND = new BashElementType("function definition shell command");
    public final IElementType GROUP_COMMAND = new BashElementType("group command");

    //arithmetic commands
    public final IElementType ARITHMETIC_COMMAND = new BashElementType("arithmetic command");

    public final IElementType ARITH_ASSIGNMENT_ELEMENT = new BashElementType("arithmetic assignment");
    public final IElementType ARITH_SUM_ELEMENT = new BashElementType("arithmetic sum");
    public final IElementType ARITH_BIT_OR_ELEMENT = new BashElementType("arithmetic bitwise or");
    public final IElementType ARITH_BIT_XOR_ELEMENT = new BashElementType("arithmetic bitwise xor");
    public final IElementType ARITH_BIT_AND_ELEMENT = new BashElementType("arithmetic bitwise and");
    public final IElementType ARITH_COMBINATION_ASSIGNMENT_ELEMENT = new BashElementType("arith assignment");
    public final IElementType ARITH_COMPUND_COMPARISION_ELEMENT = new BashElementType("arith compund comparision");
    public final IElementType ARITH_EQUALITY_ELEMENT = new BashElementType("arithmetic equality");
    public final IElementType ARITH_EXPONENT_ELEMENT = new BashElementType("arithmetic exponent");
    public final IElementType ARITH_LOGIC_AND_ELEMENT = new BashElementType("arithmetic logic and");
    public final IElementType ARITH_LOGIC_OR_ELEMENT = new BashElementType("arithmetic logic or");
    public final IElementType ARITH_MULTIPLICACTION_ELEMENT = new BashElementType("arithmetic multiplication");
    public final IElementType ARITH_NEGATION_ELEMENT = new BashElementType("arithmetic negation");
    public final IElementType ARITH_POST_INCR_ELEMENT = new BashElementType("arithmetic post incr");
    public final IElementType ARITH_PRE_INC_ELEMENT = new BashElementType("arithmetic pre incr");
    public final IElementType ARITH_SHIFT_ELEMENT = new BashElementType("arithmetic shift");
    public final IElementType ARITH_SIMPLE_ELEMENT = new BashElementType("arithmetic simple");
    public final IElementType ARITH_SIMPLE_ASSIGNMENT_ELEMENT = new BashElementType("arithmetic simple assignment");

    public final IElementType ARITH_PARENS_ELEMENT = new BashElementType("arithmetic parenthesis expr");

    public final IElementType CASE_COMMAND = new BashElementType("case pattern");
    public final IElementType CASE_PATTERN_LIST_ELEMENT = new BashElementType("case pattern list");
    public final IElementType CASE_PATTERN_ELEMENT = new BashElementType("case pattern");

    public final IElementType TIME_COMMAND = new BashElementType("time with optional -p");

    //misc
    public final IElementType EXPANSION_ELEMENT = new BashElementType("single bash expansion");


    //heredoc
    final IElementType HEREDOC_ELEMENT = new BashElementType("here doc element");
    final IElementType HEREDOC_START_MARKER_ELEMENT = new BashElementType("here doc start marker element");
    final IElementType HEREDOC_END_MARKER_ELEMENT = new BashElementType("here doc end marker element");

    final IElementType STRING_ELEMENT = new BashElementType("string");
}
