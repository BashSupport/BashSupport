/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashElementTypes.java, Class: BashElementTypes
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
    IFileElementType FILE = new IFileElementType(BashFileType.BASH_LANGUAGE);

    IElementType FILE_REFERENCE = new BashElementType("File reference");

    IElementType SHEBANG_ELEMENT = new BashElementType("shebang element");

    IElementType BLOCK_ELEMENT = new BashElementType("block element");

    //Var usage
    //fixme probably unnecessary
    IElementType VAR_ELEMENT = new BashElementType("variable");
    IElementType VAR_COMPOSED_VAR_ELEMENT = new BashElementType("composed variable, like subshell");
    IElementType PARSED_WORD_ELEMENT = new BashElementType("combined word");
    IElementType PARAM_EXPANSION_ELEMENT = new BashElementType("var substitution");

    IElementType SYMBOL_ELEMENT = new BashElementType("named symbol");

    //redirect elements
    IElementType REDIRECT_LIST_ELEMENT = new BashElementType("redirect list");
    IElementType REDIRECT_ELEMENT = new BashElementType("redirect element");

    IElementType PROCESS_SUBSTITUTION_ELEMENT = new BashElementType("process substitution element");

    //command elements
    IElementType SIMPLE_COMMAND_ELEMENT = new BashElementType("simple command element");
    IElementType VAR_DEF_ELEMENT = new BashElementType("assignment command element");
    IElementType GENERIC_COMMAND_ELEMENT = new BashElementType("generic bash command");
    IElementType INCLUDE_COMMAND_ELEMENT = new BashElementType("include command");

    //pipeline commands
    IElementType PIPELINE_COMMAND = new BashElementType("pipeline command");

    //composed command, i.e. a && b
    IElementType COMPOSED_COMMAND = new BashElementType("composed command");

    //shell commands
    IElementType WHILE_COMMAND = new BashElementType("while loop");
    IElementType UNTIL_COMMAND = new BashElementType("until loop");
    IElementType FOR_COMMAND = new BashElementType("for shellcommand");
    IElementType SELECT_COMMAND = new BashElementType("select command");
    IElementType IF_COMMAND = new BashElementType("if shellcommand");
    IElementType CONDITIONAL_COMMAND = new BashElementType("conditional shellcommand");
    IElementType SUBSHELL_COMMAND = new BashElementType("subshell shellcommand");
    IElementType BACKQUOTE_COMMAND = new BashElementType("backquote shellcommand");
    IElementType FUNCTION_DEF_COMMAND = new BashElementType("function definition shell command");
    IElementType GROUP_COMMAND = new BashElementType("group command");

    IElementType CONDITIONAL_EXPRESSION = new BashElementType("conditional / test expression");

    //arithmetic commands
    IElementType ARITHMETIC_COMMAND = new BashElementType("arithmetic command");

    IElementType ARITH_ASSIGNMENT_CHAIN_ELEMENT = new BashElementType("arithmetic assignment chain");
    IElementType ARITH_ASSIGNMENT_ELEMENT = new BashElementType("arithmetic assignment");
    IElementType ARITH_SUM_ELEMENT = new BashElementType("arithmetic sum");
    IElementType ARITH_BIT_OR_ELEMENT = new BashElementType("arithmetic bitwise or");
    IElementType ARITH_BIT_XOR_ELEMENT = new BashElementType("arithmetic bitwise xor");
    IElementType ARITH_BIT_AND_ELEMENT = new BashElementType("arithmetic bitwise and");
    IElementType ARITH_COMBINATION_ASSIGNMENT_ELEMENT = new BashElementType("arith assignment");
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
    IElementType ARITH_SIMPLE_ASSIGNMENT_ELEMENT = new BashElementType("arithmetic simple assignment");
    IElementType ARITH_TERNERAY_ELEMENT = new BashElementType("arithmetic ternary operator");

    IElementType ARITH_PARENS_ELEMENT = new BashElementType("arithmetic parenthesis expr");

    IElementType CASE_COMMAND = new BashElementType("case pattern");
    IElementType CASE_PATTERN_LIST_ELEMENT = new BashElementType("case pattern list");
    IElementType CASE_PATTERN_ELEMENT = new BashElementType("case pattern");

    IElementType TIME_COMMAND = new BashElementType("time with optional -p");

    //misc
    IElementType EXPANSION_ELEMENT = new BashElementType("single bash expansion");

    IElementType VAR_ASSIGNMENT_LIST = new BashElementType("array assignment list");

    //heredoc
    IElementType HEREDOC_ELEMENT = new BashElementType("here doc element");
    IElementType HEREDOC_START_MARKER_ELEMENT = new BashElementType("here doc start marker element");
    IElementType HEREDOC_END_MARKER_ELEMENT = new BashElementType("here doc end marker element");

    IElementType STRING_ELEMENT = new BashElementType("string");
}
