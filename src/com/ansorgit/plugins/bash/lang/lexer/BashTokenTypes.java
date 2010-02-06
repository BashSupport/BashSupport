/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTokenTypes.java, Class: BashTokenTypes
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

package com.ansorgit.plugins.bash.lang.lexer;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Identifies all of the token types (at least, the ones we'll care about) in Arc.
 * Used by the lexer to break a Bash source file down into tokens.
 *
 * @author Joachim Ansorg
 */
public interface BashTokenTypes {
    IElementType BAD_CHARACTER = TokenType.BAD_CHARACTER;

    // common types
    IElementType WHITESPACE = TokenType.WHITE_SPACE;
    TokenSet whitespace = TokenSet.create(WHITESPACE);

    IElementType NUMBER = new BashElementType("number");
    IElementType WORD = new BashElementType("word");
    IElementType ASSIGNMENT_WORD = new BashElementType("assignment_word"); //"a" =2
    IElementType ARRAY_ASSIGNMENT_WORD = new BashElementType("array_assignment_word");//"a[1]" =1
    IElementType DOLLAR = new BashElementType("$");

    IElementType LEFT_PAREN = new BashElementType("(");
    IElementType RIGHT_PAREN = new BashElementType(")");

    IElementType LEFT_CURLY = new BashElementType("{");
    IElementType RIGHT_CURLY = new BashElementType("}");

    IElementType LEFT_SQUARE = new BashElementType("[ (left square)");
    IElementType RIGHT_SQUARE = new BashElementType("] (right square)");

    // comments
    IElementType COMMENT = new BashElementType("Comment");
    IElementType SHEBANG = new BashElementType("Shebang");

    TokenSet comments = TokenSet.create(COMMENT);

    // bash reserved keywords, in alphabetic order
    IElementType BANG_TOKEN = new BashElementType("!"); //!
    IElementType CASE_KEYWORD = new BashElementType("case"); //case
    IElementType DO_KEYWORD = new BashElementType("do"); //do
    IElementType DONE_KEYWORD = new BashElementType("done"); //done
    IElementType ELIF_KEYWORD = new BashElementType("elif");//elif
    IElementType ELSE_KEYWORD = new BashElementType("else");//else
    IElementType ESAC_KEYWORD = new BashElementType("esac"); //esac
    IElementType FI_KEYWORD = new BashElementType("fi");//fi
    IElementType FOR_KEYWORD = new BashElementType("for");//for
    IElementType FUNCTION_KEYWORD = new BashElementType("function");//function
    IElementType IF_KEYWORD = new BashElementType("if");//if
    IElementType IN_KEYWORD = new BashElementType("in");//in
    IElementType SELECT_KEYWORD = new BashElementType("select");//select
    IElementType THEN_KEYWORD = new BashElementType("then");//then
    IElementType UNTIL_KEYWORD = new BashElementType("until");//until
    IElementType WHILE_KEYWORD = new BashElementType("while");//while
    IElementType TIME_KEYWORD = new BashElementType("time");//time
    IElementType BRACKET_KEYWORD = new BashElementType("[[ (left bracket)");//[[
    IElementType _BRACKET_KEYWORD = new BashElementType("]] (right bracket)");//]]

    //case state
    IElementType CASE_END = new BashElementType(";;");//;; for case expressions

    // arithmetic expressions
    IElementType EXPR_ARITH = new BashElementType("((");//))
    IElementType _EXPR_ARITH = new BashElementType("))");//]] after a $((

    //conditional expressions
    IElementType EXPR_CONDITIONAL = new BashElementType("[ (left conditional)");//"[ "
    IElementType _EXPR_CONDITIONAL = new BashElementType(" ] (right conditional)");//" ]"

    TokenSet keywords = TokenSet.create(BANG_TOKEN, CASE_KEYWORD, DO_KEYWORD, DONE_KEYWORD,
            ELIF_KEYWORD, ELSE_KEYWORD, ESAC_KEYWORD, FI_KEYWORD, FOR_KEYWORD, FUNCTION_KEYWORD,
            IF_KEYWORD, IN_KEYWORD, SELECT_KEYWORD, THEN_KEYWORD, UNTIL_KEYWORD, WHILE_KEYWORD,
            TIME_KEYWORD, BRACKET_KEYWORD, _BRACKET_KEYWORD,
            CASE_END, DOLLAR,
            EXPR_ARITH, _EXPR_ARITH, EXPR_CONDITIONAL, _EXPR_CONDITIONAL);

    //here doc and here string
    IElementType HERE_STRING = new BashElementType("<<<");//<<<
    //fixme: missing here doc <<- word\n...\ndelimiter

    // single characters
    IElementType BACKSLASH = new BashElementType("\\");
    IElementType AMP = new BashElementType("&");
    IElementType AT = new BashElementType("@");
    IElementType COLON = new BashElementType(":");
    IElementType COMMA = new BashElementType(",");
    IElementType EQ = new BashElementType("=");
    IElementType ADD_EQ = new BashElementType("+=");
    IElementType SEMI = new BashElementType(";");
    IElementType SHIFT_RIGHT = new BashElementType(">>");//>>
    IElementType LESS_THAN = new BashElementType("<");//>>
    IElementType GREATER_THAN = new BashElementType(">");//>>

    IElementType PIPE = new BashElementType("|");// }
    IElementType PIPE_AMP = new BashElementType("|&"); //bash 4 only, equivalent to 2>&1 |
    IElementType AND_AND = new BashElementType("&&");//!=
    IElementType OR_OR = new BashElementType("||");//!=

    IElementType LINE_FEED = new BashElementType("linefeed");// }

    final TokenSet pipeTokens = TokenSet.create(PIPE, PIPE_AMP);


    //arithmetic comparision

    TokenSet arithmeticCompareOps = TokenSet.create();


    //arithmetic operators: plus
    IElementType ARITH_PLUS_PLUS = new BashElementType("++");//++
    IElementType ARITH_PLUS = new BashElementType("+");//+
    //arithmetic operators: minus
    IElementType ARITH_MINUS_MINUS = new BashElementType("--");//++
    IElementType ARITH_MINUS = new BashElementType("-");//+

    TokenSet arithmeticPostOps = TokenSet.create(ARITH_PLUS_PLUS, ARITH_MINUS_MINUS);
    TokenSet arithmeticPreOps = TokenSet.create(ARITH_PLUS_PLUS, ARITH_MINUS_MINUS);
    TokenSet arithmeticAdditionOps = TokenSet.create(ARITH_PLUS, ARITH_MINUS);

    TokenSet arithmeticShiftOps = TokenSet.create(SHIFT_RIGHT); //fixme add shift left

    //arithmetic operators: misc
    IElementType ARITH_EXP = new BashElementType("**");//**
    IElementType ARITH_MULT = new BashElementType("*");//*
    IElementType ARITH_DIV = new BashElementType("/");// /
    IElementType ARITH_MOD = new BashElementType("%");//%
    IElementType ARITH_SHIFT_LEFT = new BashElementType("<<");//++
    IElementType ARITH_NEGATE = new BashElementType("negation !");//||
    IElementType ARITH_BITWISE_NEGATE = new BashElementType("bitwise negation ~");//~ //fixme

    TokenSet arithmeticNegationOps = TokenSet.create(ARITH_NEGATE, ARITH_BITWISE_NEGATE);

    TokenSet arithmeticProduct = TokenSet.create(ARITH_MULT, ARITH_DIV);

    //arithmetic operators: comparision
    IElementType ARITH_LE = new BashElementType("<=");//<=
    IElementType ARITH_GE = new BashElementType(">=");//>=
    IElementType ARITH_GT = new BashElementType(">");//>=
    IElementType ARITH_LT = new BashElementType("<");//>=
    IElementType ARITH_EQ = new BashElementType("==");//==
    IElementType ARITH_NE = new BashElementType("!=");//!=

    TokenSet arithmeticCmp = TokenSet.create(ARITH_LE, ARITH_GE, ARITH_NE, ARITH_LT, ARITH_LT, ARITH_EQ);

    //arithmetic expressiong: logic
    IElementType ARITH_OR = new BashElementType("||");//||
    IElementType ARITH_AND = new BashElementType("&&");//||
    IElementType ARITH_QMARK = new BashElementType("?");//||
    IElementType ARITH_COLON = new BashElementType(":");//||
    IElementType ARITH_XOR = new BashElementType("^");//||
    //fixme missing: & ^ , |

    TokenSet arithmeticLogic = TokenSet.create(ARITH_OR, ARITH_AND);


    //arithmetic operators: assign
    IElementType ARITH_ASS_MUL = new BashElementType("*=");// *=
    IElementType ARITH_ASS_DIV = new BashElementType("/=");// /=
    IElementType ARITH_ASS_MOD = new BashElementType("%=");// /=
    IElementType ARITH_ASS_PLUS = new BashElementType("+=");// /=
    IElementType ARITH_ASS_MINUS = new BashElementType("-=");// /=
    IElementType ARITH_ASS_SHIFT_RIGHT = new BashElementType(">>=");// /=
    IElementType ARITH_ASS_SHIFT_LEFT = new BashElementType("<<=");// /=
//    IElementType ARITH_ASS_SHIFT_AND = new BashElementType("/=");// /=
    //fixme missing: &= |= ^=, = ","

    TokenSet arithmeticAssign = TokenSet.create(ARITH_ASS_MUL, ARITH_ASS_DIV, ARITH_ASS_MOD, ARITH_ASS_PLUS,
            ARITH_ASS_MINUS, ARITH_ASS_SHIFT_LEFT, ARITH_ASS_SHIFT_RIGHT);


    //builtin command
    IElementType COMMAND_TOKEN = new BashElementType("command");//!=
    TokenSet commands = TokenSet.create(COMMAND_TOKEN);

    //variables
    IElementType VARIABLE = new BashElementType("variable");

    //parameter expansion
    IElementType PARAM_EXPANSION_OP = new BashElementType("Parameter expansion operator");

    // Special characters
    IElementType STRING_BEGIN = new BashElementType("string begin");
    IElementType STRING_CHAR = new BashElementType("string char");
    IElementType STRING_END = new BashElementType("string end");

    IElementType STRING2 = new BashElementType("unevaluated string");
    IElementType BACKQUOTE = new BashElementType("backquote `");
    IElementType INTERNAL_COMMAND = new BashElementType("internal bash command");

    IElementType INTEGER_LITERAL = new BashElementType("int literal");
    //fixme dot and colon?
    TokenSet stringLiterals = TokenSet.create(WORD, STRING2, INTEGER_LITERAL, COLON, INTERNAL_COMMAND);
    TokenSet editorStringLiterals = TokenSet.create(STRING2);

    // test Operators
    IElementType COND_OP = new BashElementType("cond_op");//all the test operators, e.g. -z, != ...
    TokenSet conditionalOperators = TokenSet.create(COND_OP, OR_OR, AND_AND);

    //redirects
    IElementType REDIRECT_LESS_LESS = new BashElementType("<<");//[[
    IElementType REDIRECT_LESS_LESS_LESS = new BashElementType("<<<");//
    IElementType REDIRECT_LESS_AND = new BashElementType("<&");//[[
    IElementType REDIRECT_GREATER_AND = new BashElementType(">&");//[[
    IElementType REDIRECT_LESS_LESS_MINUS = new BashElementType("<<-");//[[
    IElementType REDIRECT_LESS_GREATER = new BashElementType("<>");//[[
    IElementType REDIRECT_GREATER_BAR = new BashElementType(">|");//[[
    //Bash 4: 
    IElementType REDIRECT_AMP_GREATER_GREATER = new BashElementType("&>>");

    //fixme missing: &>  >&

    TokenSet redirectionSet = TokenSet.create(
            GREATER_THAN, LESS_THAN, SHIFT_RIGHT,
            REDIRECT_LESS_LESS, REDIRECT_LESS_LESS_LESS, REDIRECT_LESS_AND,
            REDIRECT_GREATER_AND, REDIRECT_LESS_LESS_MINUS, REDIRECT_LESS_GREATER, REDIRECT_GREATER_BAR,
            REDIRECT_AMP_GREATER_GREATER, PIPE_AMP);

    //sets
    TokenSet identifierTokenSet = TokenSet.orSet(keywords, TokenSet.create(INTERNAL_COMMAND));
}