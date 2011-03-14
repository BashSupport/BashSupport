/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLexerTest.java, Class: BashLexerTest
 * Last modified: 2010-11-02
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

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.intellij.psi.tree.IElementType;
import org.junit.Assert;
import org.junit.Test;

import static com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes.*;

/**
 * Tests the bash lexer.
 *
 * @author Joachim Ansorg
 */
public class BashLexerTest {
    @Test
    public void testSimpleDefTokenization() {
        testTokenization("#", COMMENT);
        testTokenization("# ", COMMENT);
        testTokenization("# Text", COMMENT);
        testTokenization("# Text\n", COMMENT, LINE_FEED);
        testTokenization("a #!b", WORD, WHITESPACE, SHEBANG);
        testTokenization("#a\n#b\n", COMMENT, LINE_FEED, COMMENT, LINE_FEED);
    }

    @Test
    public void testVariables() {
        testTokenization("$abc", VARIABLE);

        testTokenization("$a$", VARIABLE, DOLLAR);
        testTokenization("$", DOLLAR);
        //testTokenization("${?}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);

        testTokenization("$(echo)", DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, RIGHT_PAREN);
        testTokenization("${echo}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${#echo}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_LENGTH, WORD, RIGHT_CURLY);
        testTokenization("${!echo}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, WORD, RIGHT_CURLY);
        testTokenization("a ${a# echo} a", WORD, WHITESPACE, DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_LENGTH, WHITESPACE, WORD, RIGHT_CURLY, WHITESPACE, WORD);
        testTokenization("${echo} ${echo}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, WHITESPACE, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("a=1 b=2 echo", ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, INTERNAL_COMMAND);
        testTokenization("a=1 b=2", ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);
        testTokenization("a+=a", ASSIGNMENT_WORD, ADD_EQ, WORD);
        testTokenization("if a; then PIDDIR=a$(a) a; fi", IF_KEYWORD, WHITESPACE, WORD, SEMI, WHITESPACE, THEN_KEYWORD, WHITESPACE, ASSIGNMENT_WORD, EQ, WORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, WORD, SEMI, WHITESPACE, FI_KEYWORD);
        //line continuation token is ignored
        testTokenization("a=a\\\nb", ASSIGNMENT_WORD, EQ, WORD, WORD);

        testTokenization("[ $(uname -a) ]", EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, RIGHT_PAREN, _EXPR_CONDITIONAL);

        //testTokenization("a[$a]", ARRAY_ASSIGNMENT_WORD);//fixme
    }

    @Test
    public void testArrayVariables() throws Exception {
        testTokenization("${PIPESTATUS[0]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${#myVar[*]}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_LENGTH, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${myVar[0]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("${myVar[*]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("${myVar[@]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("a=( one two three)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_PAREN);

        testTokenization("a=( one two [2]=three)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, LEFT_SQUARE, INTEGER_LITERAL, RIGHT_SQUARE, EQ, WORD, RIGHT_PAREN);

        testTokenization("a[1]=", ASSIGNMENT_WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, EQ);
    }

    @Test
    public void testSquareBracketArithmeticExpr() {
        testTokenization("$[1]", DOLLAR, EXPR_ARITH_SQUARE, NUMBER, _EXPR_ARITH_SQUARE);

        testTokenization("$[1 ]", DOLLAR, EXPR_ARITH_SQUARE, NUMBER, WHITESPACE, _EXPR_ARITH_SQUARE);

        testTokenization("$[1/${a}]", DOLLAR, EXPR_ARITH_SQUARE, NUMBER, ARITH_DIV, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_ARITH_SQUARE);

        //lexable, but bad syntax
        testTokenization("$(([1]))", DOLLAR, EXPR_ARITH, LEFT_SQUARE, NUMBER, RIGHT_SQUARE, _EXPR_ARITH);
    }

    @Test
    public void testArithmeticExpr() {
        testTokenization("$((1))", DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH);

        testTokenization("$((1,1))", DOLLAR, EXPR_ARITH, NUMBER, COMMA, NUMBER, _EXPR_ARITH);

        testTokenization("$((1/${a}))", DOLLAR, EXPR_ARITH, NUMBER, ARITH_DIV, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_ARITH);

        testTokenization("$((a=1,1))", DOLLAR, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, COMMA, NUMBER, _EXPR_ARITH);

        testTokenization("$((((1))))", DOLLAR, EXPR_ARITH, LEFT_PAREN, LEFT_PAREN, NUMBER, RIGHT_PAREN, RIGHT_PAREN, _EXPR_ARITH);

        testTokenization("$((-1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, NUMBER, _EXPR_ARITH);

        testTokenization("$((--1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, NUMBER, _EXPR_ARITH);

        testTokenization("$((--a))", DOLLAR, EXPR_ARITH, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((- --a))", DOLLAR, EXPR_ARITH, ARITH_MINUS, WHITESPACE, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((-1 -1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, NUMBER, WHITESPACE, ARITH_MINUS, NUMBER, _EXPR_ARITH);

        testTokenization("$((a & b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, ARITH_BITWISE_AND, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((a && b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((a || b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((!a))", DOLLAR, EXPR_ARITH, ARITH_NEGATE, WORD, _EXPR_ARITH);

        testTokenization("$((~a))", DOLLAR, EXPR_ARITH, ARITH_BITWISE_NEGATE, WORD, _EXPR_ARITH);

        testTokenization("$((a>>2))", DOLLAR, EXPR_ARITH, WORD, ARITH_SHIFT_RIGHT, NUMBER, _EXPR_ARITH);

        testTokenization("$((a<<2))", DOLLAR, EXPR_ARITH, WORD, ARITH_SHIFT_LEFT, NUMBER, _EXPR_ARITH);

        testTokenization("$((a|2))", DOLLAR, EXPR_ARITH, WORD, PIPE, NUMBER, _EXPR_ARITH);

        testTokenization("$((a&2))", DOLLAR, EXPR_ARITH, WORD, ARITH_BITWISE_AND, NUMBER, _EXPR_ARITH);

        testTokenization("$((a^2))", DOLLAR, EXPR_ARITH, WORD, ARITH_BITWISE_XOR, NUMBER, _EXPR_ARITH);

        testTokenization("$((a%2))", DOLLAR, EXPR_ARITH, WORD, ARITH_MOD, NUMBER, _EXPR_ARITH);

        testTokenization("$((a-2))", DOLLAR, EXPR_ARITH, WORD, ARITH_MINUS, NUMBER, _EXPR_ARITH);

        testTokenization("$((a--))", DOLLAR, EXPR_ARITH, WORD, ARITH_MINUS_MINUS, _EXPR_ARITH);

        testTokenization("$((--a))", DOLLAR, EXPR_ARITH, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((a,2))", DOLLAR, EXPR_ARITH, WORD, COMMA, NUMBER, _EXPR_ARITH);

        testTokenization("$((a>2))", DOLLAR, EXPR_ARITH, WORD, ARITH_GT, NUMBER, _EXPR_ARITH);

        testTokenization("$((a > 2))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, ARITH_GT, WHITESPACE, NUMBER, _EXPR_ARITH);

        testTokenization("$((a>=2))", DOLLAR, EXPR_ARITH, WORD, ARITH_GE, NUMBER, _EXPR_ARITH);

        testTokenization("$((a<2))", DOLLAR, EXPR_ARITH, WORD, ARITH_LT, NUMBER, _EXPR_ARITH);

        testTokenization("$((a<=2))", DOLLAR, EXPR_ARITH, WORD, ARITH_LE, NUMBER, _EXPR_ARITH);

        testTokenization("$((1+-45))", DOLLAR, EXPR_ARITH, NUMBER, ARITH_PLUS, ARITH_MINUS, NUMBER, _EXPR_ARITH);

        testTokenization("$((1+(-45)))", DOLLAR, EXPR_ARITH, NUMBER, ARITH_PLUS, LEFT_PAREN, ARITH_MINUS, NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        testTokenization("$((1+---45))", DOLLAR, EXPR_ARITH, NUMBER, ARITH_PLUS, ARITH_MINUS, ARITH_MINUS, ARITH_MINUS, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testShebang() {
        testTokenization("#!", SHEBANG);

        testTokenization("#!/bin/bash", SHEBANG);

        testTokenization("#!/bin/bash\n", SHEBANG);

        testTokenization("#!/bin/bash\n\r", SHEBANG, LINE_FEED);

        testTokenization("\n#!/bin/bash", LINE_FEED, SHEBANG);
    }

    @Test
    public void testWhitespace() {
        testTokenization(" ", WHITESPACE);

        testTokenization("\t", WHITESPACE);

        testTokenization("\f", WHITESPACE);

        testTokenization(" \\\n ", WHITESPACE, WHITESPACE);
    }

    @Test
    public void testIdentifier() {
        testTokenization("a", WORD);

        testTokenization("ab", WORD);

        testTokenization("abc123", WORD);

        testTokenization("ABC_123", WORD);
    }

    @Test
    public void testStrings() {
        testTokenization("\"\"", STRING_BEGIN, STRING_END);
        testTokenization("\"abc\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"abc\"\"abc\"", STRING_BEGIN, WORD, STRING_END, STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"\\.\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"\\n\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\" \"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"$( a )\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WHITESPACE, WORD, WHITESPACE, RIGHT_PAREN, STRING_END);
        testTokenization("\"$a\"", STRING_BEGIN, VARIABLE, STRING_END);
        testTokenization("\"a b\\\"\"", STRING_BEGIN, WORD, WORD, STRING_END);
        testTokenization("a b \"a b \\\"\" \"a\" b",
                WORD, WHITESPACE, WORD, WHITESPACE, STRING_BEGIN, WORD, WORD, STRING_END, WHITESPACE,
                STRING_BEGIN, WORD, STRING_END, WHITESPACE, WORD);
        testTokenization("\"a$\"", STRING_BEGIN, WORD, DOLLAR, STRING_END);

        testTokenization("\"$(\"hey there\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        //        testTokenization("\"$(\"hey there\\\"\")\" \\\" \"$(\"hey there\")\" a",
        //                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WORD, RIGHT_PAREN, STRING_END, STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"$(echo \\\"\\\")\"",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD,
                WHITESPACE, WORD, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(echo \\\"\\\")\" a",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, WORD, RIGHT_PAREN, STRING_END,
                WHITESPACE, WORD);

        testTokenization("\"$(echo || echo)\"",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(echo && echo)\"",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(abc)\"",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(1)\"",
                STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$((1))\"",
                STRING_BEGIN, DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH, STRING_END);

        testTokenization("\\.", WORD);
        testTokenization("\\n", WORD);
        testTokenization("\\>", WORD);
        testTokenization("\\<", WORD);

        testTokenization("\"||\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"$(||)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, OR_OR, RIGHT_PAREN, STRING_END);

        testTokenization("\"&&\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"$(&&)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, AND_AND, RIGHT_PAREN, STRING_END);

        testTokenization("a#%%", WORD);

        testTokenization("a#%%[0-9]", WORD);

        testTokenization("echo level%%[a-zA-Z]*", INTERNAL_COMMAND, WHITESPACE, WORD);
        testTokenization("[ \\${a} ]", EXPR_CONDITIONAL, WORD, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_CONDITIONAL);
        testTokenization("[ a  ]", EXPR_CONDITIONAL, WORD, WHITESPACE, _EXPR_CONDITIONAL);
        testTokenization("[ a | b ]", EXPR_CONDITIONAL, WORD, WHITESPACE, PIPE, WHITESPACE, WORD, _EXPR_CONDITIONAL);
        testTokenization("[[ a || b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization("${rdev%:*?}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, PARAM_EXPANSION_OP_COLON, PARAM_EXPANSION_OP_STAR, PARAM_EXPANSION_OP_UNKNOWN, RIGHT_CURLY);
        testTokenization("${@!-+}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_AT, PARAM_EXPANSION_OP_EXCL, PARAM_EXPANSION_OP_MINUS, PARAM_EXPANSION_OP_PLUS, RIGHT_CURLY);
        testTokenization("${a[@]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);
        testTokenization("${\\ }", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);

        testTokenization("$\"\"", STRING_BEGIN, STRING_END);
        testTokenization("$\"abc\"", STRING_BEGIN, WORD, STRING_END);

        testTokenization("$''", STRING2);
        testTokenization("$'abc'", STRING2);

        //multiline strings
        testTokenization("\"a\nb\nc\"", STRING_BEGIN, WORD, STRING_END);
        testTokenization("\"\n\"", STRING_BEGIN, WORD, STRING_END);
        //multiline string2
        testTokenization("'a\nb\nc'", STRING2);
        testTokenization("'\n'", STRING2);

        //test escaped chars
        testTokenization("\\*", WORD);
        testTokenization("\\ ", WORD);
        testTokenization("\\{", WORD);
        testTokenization("\\;", WORD);
        testTokenization("\\.", WORD);
        testTokenization("\\r", WORD);
        testTokenization("\\n", WORD);
        testTokenization("\\:", WORD);
        testTokenization("\\(", WORD);
        testTokenization("\\)", WORD);
        testTokenization("\\\"", WORD);
        testTokenization("\\\\", WORD);
        testTokenization("\\>", WORD);
        testTokenization("\\<", WORD);
        testTokenization("\\$", WORD);
        testTokenization("\\ ", WORD);
        testTokenization("\\?", WORD);
        testTokenization("\\!", WORD);
        //fixme: line continuation, check with spec
        //testTokenization("abc\\\nabc", WORD);

        //subshells
        testTokenization("\"$( () )\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WHITESPACE, LEFT_PAREN,
                RIGHT_PAREN, WHITESPACE, RIGHT_PAREN, STRING_END);
        testTokenization("\"$( () )\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WHITESPACE, LEFT_PAREN,
                RIGHT_PAREN, WHITESPACE, RIGHT_PAREN, STRING_END);
    }

    @Test
    public void testWords() {
        testTokenization("%", WORD);
        testTokenization("a%b", WORD);
        testTokenization("a%b}", WORD, RIGHT_CURLY);
        testTokenization("echo level%%[a-zA-Z]*", INTERNAL_COMMAND, WHITESPACE, WORD);
        testTokenization("tr [:upper:]", WORD, WHITESPACE, LEFT_SQUARE, WORD);
    }

    @Test
    public void testInternalCommands() {
        testTokenization("declare", INTERNAL_COMMAND);
        testTokenization("echo", INTERNAL_COMMAND);
        testTokenization("export", INTERNAL_COMMAND);
        testTokenization("readonly", INTERNAL_COMMAND);
        testTokenization("local", INTERNAL_COMMAND);

        testTokenization(".", INTERNAL_COMMAND);
        testTokenization(". /home/user/bashrc", INTERNAL_COMMAND, WHITESPACE, WORD);
        testTokenization(". /home/user/bashrc$a", INTERNAL_COMMAND, WHITESPACE, WORD, VARIABLE);
    }

    @Test
    public void testExpressions() {
        testTokenization("if [ -n \"a\" ]; then a; fi;",
                IF_KEYWORD, WHITESPACE, EXPR_CONDITIONAL, COND_OP, WHITESPACE, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, SEMI,
                WHITESPACE, THEN_KEYWORD, WHITESPACE, WORD, SEMI, WHITESPACE, FI_KEYWORD, SEMI);

        testTokenization("$((1+2))", DOLLAR, EXPR_ARITH, NUMBER, ARITH_PLUS, NUMBER, _EXPR_ARITH);
        testTokenization("((i=$(echo 1)))", EXPR_ARITH, ASSIGNMENT_WORD, EQ, DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN, _EXPR_ARITH);
        testTokenization("((i=$((1 + 9))))",
                EXPR_ARITH, ASSIGNMENT_WORD, EQ, DOLLAR, EXPR_ARITH, NUMBER, WHITESPACE,
                ARITH_PLUS, WHITESPACE, NUMBER, _EXPR_ARITH, _EXPR_ARITH);

        testTokenization("((1 == 1 ? 0 : 0))",
                EXPR_ARITH, NUMBER, WHITESPACE, ARITH_EQ, WHITESPACE, NUMBER, WHITESPACE, ARITH_QMARK,
                WHITESPACE, NUMBER, WHITESPACE, ARITH_COLON, WHITESPACE, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testSubshell() {
        testTokenization("$(echo \"$1\")",
                DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, RIGHT_PAREN);
        testTokenization("$(($(echo \"$1\")))",
                DOLLAR, EXPR_ARITH, DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, RIGHT_PAREN, _EXPR_ARITH);
        testTokenization("`for d in`",
                BACKQUOTE, FOR_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, BACKQUOTE);
    }

    @Test
    public void testNumber() {
        testTokenization("123", INTEGER_LITERAL);
        testTokenization("$((123))", DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH);

        testTokenization("123 456", INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL);
        testTokenization("$((123 234))", DOLLAR, EXPR_ARITH, NUMBER, WHITESPACE, NUMBER, _EXPR_ARITH);
    }

    @Test
    public void testFunction() {
        testTokenization("function", FUNCTION_KEYWORD);
    }

    @Test
    public void testVariable() {
        testTokenization("$a", VARIABLE);
        testTokenization("$abc", VARIABLE);
        testTokenization("$abc123_", VARIABLE);
        testTokenization("$$", VARIABLE);
        testTokenization("$1", VARIABLE);
        testTokenization("$*", VARIABLE);
        testTokenization("}", RIGHT_CURLY);
        testTokenization("${1}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${a}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${a%}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, RIGHT_CURLY);
        testTokenization("${a%b}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, WORD, RIGHT_CURLY);
        testTokenization("${#a}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_LENGTH, WORD, RIGHT_CURLY);
        testTokenization("${a1}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${/}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_UNKNOWN, RIGHT_CURLY);
    }

    @Test
    public void testRedirect1() {
        testTokenization(">&2", GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("<&1", LESS_THAN, FILEDESCRIPTOR);
        testTokenization("<<", REDIRECT_LESS_LESS);
        testTokenization("<<<", REDIRECT_LESS_LESS_LESS);
        testTokenization("<<-", REDIRECT_LESS_LESS_MINUS);
        testTokenization("<>", REDIRECT_LESS_GREATER);
        testTokenization(">|", REDIRECT_GREATER_BAR);
        testTokenization(">1", GREATER_THAN, INTEGER_LITERAL);
        testTokenization("> 1", GREATER_THAN, WHITESPACE, INTEGER_LITERAL);
        testTokenization(">&1", GREATER_THAN, FILEDESCRIPTOR);

        testTokenization("<&-", LESS_THAN, FILEDESCRIPTOR);
        testTokenization(">&-", GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("1>&-", INTEGER_LITERAL, GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("1>&-", INTEGER_LITERAL, GREATER_THAN, FILEDESCRIPTOR);

        testTokenization("3>&9", INTEGER_LITERAL, GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("3>&10", INTEGER_LITERAL, GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("10>&10", INTEGER_LITERAL, GREATER_THAN, FILEDESCRIPTOR);
        testTokenization("10>&a123", INTEGER_LITERAL, REDIRECT_GREATER_AMP, WORD);
        testTokenization("10>& a123", INTEGER_LITERAL, REDIRECT_GREATER_AMP, WHITESPACE, WORD);

        testTokenization("$(a >&1)", DOLLAR, LEFT_PAREN, WORD, WHITESPACE, GREATER_THAN, FILEDESCRIPTOR, RIGHT_PAREN);
    }

    @Test
    public void testConditional() {
        testTokenization("[ 1 = \"$backgrounded\" ]", EXPR_CONDITIONAL, WORD, WHITESPACE, COND_OP, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, _EXPR_CONDITIONAL);
    }

    @Test
    public void testBracket() {
        testTokenization("[[ -f test.txt ]]", BRACKET_KEYWORD, WORD, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization(" ]]", _BRACKET_KEYWORD);
        testTokenization("  ]]", WHITESPACE, _BRACKET_KEYWORD);
        testTokenization("[[  -f test.txt   ]]", BRACKET_KEYWORD, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, WHITESPACE, _BRACKET_KEYWORD);
    }

    @Test
    public void testParameterSubstitution() {
        testTokenization("${a=x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_EQ, WORD, RIGHT_CURLY);
        testTokenization("${a:=x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_EQ, WORD, RIGHT_CURLY);

        testTokenization("${a-x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, WORD, RIGHT_CURLY);
        testTokenization("${a:-x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, WORD, RIGHT_CURLY);

        testTokenization("${a+x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PLUS, WORD, RIGHT_CURLY);
        testTokenization("${a:+x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_PLUS, WORD, RIGHT_CURLY);

        testTokenization("${!a}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, WORD, RIGHT_CURLY);

        testTokenization("${a?x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_UNKNOWN, WORD, RIGHT_CURLY);
    }

    @Test
    public void testWeirdStuff1() {
        testTokenization(": > a", INTERNAL_COMMAND, WHITESPACE, GREATER_THAN, WHITESPACE, WORD);
        testTokenization("^", WORD);
        testTokenization("$!", VARIABLE);
        testTokenization("!", BANG_TOKEN);
        testTokenization("+m", WORD);
        testTokenization("\\#", WORD);
        testTokenization("a,b", WORD);
        testTokenization("~", WORD);
        testTokenization("a~", WORD);
        testTokenization("\\$", WORD);
        testTokenization("a[1]=2", ARRAY_ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);
        testTokenization("a[1]='2'", ARRAY_ASSIGNMENT_WORD, EQ, STRING2);
        testTokenization("a[1+2]='2'", ARRAY_ASSIGNMENT_WORD, EQ, STRING2);
        testTokenization("esac;", WORD, SEMI);

        //"$(echo "123")"
        testTokenization("\"$(echo 123)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(\"123\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        //testTokenization("\"$( \"a\")\"", BashElementTypes.STRING_ELEMENT);
        //testTokenization("\"$( \"123\")\"", BashElementTypes.STRING_ELEMENT);
        //testTokenization("\"$(echo \"123\")\"", BashElementTypes.STRING_ELEMENT);
        //testTokenization("\"$(echo echo \"123\")\"", BashElementTypes.STRING_ELEMENT);
        //testTokenization("\"$(echo \"$(echo \"123\")\")\"", BashElementTypes.STRING_ELEMENT);

        //tilde expansion
        testTokenization("~", WORD);
        testTokenization("~user", WORD);
        testTokenization("~+", WORD);
        testTokenization("~-", WORD);
        testTokenization("~1", WORD);
        testTokenization("~+1", WORD);
        testTokenization("~-1", WORD);

        //weird expansions
        testTokenization("echo ${feld[${index}]}", INTERNAL_COMMAND, WHITESPACE, DOLLAR, LEFT_CURLY,
                WORD, LEFT_SQUARE, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, RIGHT_SQUARE, RIGHT_CURLY);

        //shebang line after first line
        testTokenization("echo; #!/bin/sh", INTERNAL_COMMAND, SEMI, WHITESPACE, SHEBANG);
    }

    @Test
    public void testBackquote1() {
        testTokenization("`", BACKQUOTE);
        testTokenization("``", BACKQUOTE, BACKQUOTE);
        testTokenization("`echo a`", BACKQUOTE, INTERNAL_COMMAND, WHITESPACE, WORD, BACKQUOTE);
        testTokenization("`\\``", BACKQUOTE, WORD, BACKQUOTE);
    }

    @Test
    public void testCasePattern() {
        testTokenization("case a in a=a);; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN,
                CASE_END, WHITESPACE, ESAC_KEYWORD);

        testTokenization("case a in a/ui);; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN,
                CASE_END, WHITESPACE, ESAC_KEYWORD);

        testTokenization("case a in a#);; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN,
                CASE_END, WHITESPACE, ESAC_KEYWORD);

        testTokenization("case a in\n  a#);; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                LINE_FEED, WHITESPACE, WHITESPACE,
                WORD, RIGHT_PAREN, CASE_END, WHITESPACE, ESAC_KEYWORD);

        //v3 vs. v4 changes in end marker
        testTokenization(BashVersion.Bash_v4, "case a in a);;& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, WHITESPACE, ESAC_KEYWORD);
        testTokenization(BashVersion.Bash_v3, "case a in a);;& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, WORD, WHITESPACE, ESAC_KEYWORD);
        //fixme should be AMP instead of WORD in the last part

        //v3 vs. v4 changes in new end marker
        testTokenization(BashVersion.Bash_v4, "case a in a);& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, WHITESPACE, ESAC_KEYWORD);
        testTokenization(BashVersion.Bash_v3, "case a in a);& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, SEMI, WORD, WHITESPACE, ESAC_KEYWORD);
        //fixme should be AMP instead of WORD in the last part
    }

    @Test
    public void testAssignmentList() {
        testTokenization("a=(1)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, INTEGER_LITERAL, RIGHT_PAREN);
        testTokenization("a=(a,b,c)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, COMMA, WORD, COMMA, WORD, RIGHT_PAREN);
    }

    @Test
    public void testEval() {
        testTokenization("eval [ \"a\" ]",
                INTERNAL_COMMAND, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL);

        testTokenization("for f in a; do eval [ \"a\" ]; done",
                FOR_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, SEMI,
                WHITESPACE, DO_KEYWORD, WHITESPACE,
                INTERNAL_COMMAND, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL,
                SEMI, WHITESPACE, DONE_KEYWORD);

        testTokenization("case a in a) echo [ \"a\" ];; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD,
                RIGHT_PAREN, WHITESPACE, INTERNAL_COMMAND, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, CASE_END, WHITESPACE, ESAC_KEYWORD);
    }

    @Test
    public void testNestedStatements() {
        testTokenization("case a in a) for do done ;; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD,
                RIGHT_PAREN, WHITESPACE, FOR_KEYWORD, WHITESPACE, DO_KEYWORD, WHITESPACE, DONE_KEYWORD, WHITESPACE, CASE_END, WHITESPACE,
                ESAC_KEYWORD);

        testTokenization("case a in a) in ;; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD,
                RIGHT_PAREN, WHITESPACE, IN_KEYWORD, WHITESPACE, CASE_END, WHITESPACE,
                ESAC_KEYWORD);

        testTokenization("if; a; then\nb #123\nfi",
                IF_KEYWORD, SEMI, WHITESPACE, WORD, SEMI, WHITESPACE, THEN_KEYWORD, LINE_FEED,
                WORD, WHITESPACE, COMMENT, LINE_FEED, FI_KEYWORD);

        testTokenization("for ((a=1;;))",
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, SEMI,
                SEMI, _EXPR_ARITH);

        testTokenization("for ((a=1;a;))",
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, SEMI,
                WORD, SEMI, _EXPR_ARITH);

        testTokenization("for ((a=1;a<2;a=a+1))",
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, NUMBER, SEMI,
                WORD, ARITH_LT, NUMBER, SEMI, ASSIGNMENT_WORD, EQ, WORD, ARITH_PLUS, NUMBER, _EXPR_ARITH);

        testTokenization("$(read -p \"a\")",
                DOLLAR, LEFT_PAREN, INTERNAL_COMMAND, WHITESPACE, WORD, WHITESPACE, STRING_BEGIN, WORD,
                STRING_END, RIGHT_PAREN);
    }

    @Test
    public void testV4Lexing() {
        //new &>> redirect token
        testTokenization(BashVersion.Bash_v4, "a &>> out", WORD, WHITESPACE, REDIRECT_AMP_GREATER_GREATER, WHITESPACE, WORD);
        testTokenization(BashVersion.Bash_v3, "a &>> out", WORD, WHITESPACE, AMP, SHIFT_RIGHT, WHITESPACE, WORD);

        //new &> redirect token
        testTokenization(BashVersion.Bash_v4, "a &> out", WORD, WHITESPACE, REDIRECT_AMP_GREATER, WHITESPACE, WORD);
        testTokenization(BashVersion.Bash_v3, "a &> out", WORD, WHITESPACE, AMP, GREATER_THAN, WHITESPACE, WORD);

        //new |& redirect token
        testTokenization(BashVersion.Bash_v4, "a |& b", WORD, WHITESPACE, PIPE_AMP, WHITESPACE, WORD);
        testTokenization(BashVersion.Bash_v4, "\"$(a |& b)\"", STRING_BEGIN, DOLLAR,
                LEFT_PAREN, WORD, WHITESPACE, PIPE_AMP, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization(BashVersion.Bash_v3, "a |& b", WORD, WHITESPACE, PIPE, AMP, WHITESPACE, WORD);
    }

    @Test
    public void testParamExpansionNested() {
        //${a${a}}
        testTokenization("${a${b}}", DOLLAR, LEFT_CURLY, WORD,
                DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, RIGHT_CURLY);

        //${a$(a)}
        testTokenization("${a$(b)}", DOLLAR, LEFT_CURLY, WORD,
                DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, RIGHT_CURLY);

        //${a$($(a))}
        testTokenization("${a$($(b))}", DOLLAR, LEFT_CURLY, WORD,
                DOLLAR, LEFT_PAREN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, RIGHT_PAREN, RIGHT_CURLY);

        //${a$(a > b)}
        testTokenization("${a$(a > b)}", DOLLAR, LEFT_CURLY, WORD,
                DOLLAR, LEFT_PAREN, WORD, WHITESPACE, GREATER_THAN, WHITESPACE, WORD, RIGHT_PAREN, RIGHT_CURLY);

        //DIR=${$(a $(b)/..)}
        testTokenization("DIR=${$(a $(b)/..)}", ASSIGNMENT_WORD, EQ, DOLLAR, LEFT_CURLY,
                DOLLAR, LEFT_PAREN, WORD, WHITESPACE, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN,
                WORD, RIGHT_PAREN, RIGHT_CURLY);
    }

    @Test
    public void testParamExpansion() {
        testTokenization("${a}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${a:a}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);
        testTokenization("${a:-a}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, WORD, RIGHT_CURLY);
        testTokenization("${a:.*}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON, PARAM_EXPANSION_OP_UNKNOWN, PARAM_EXPANSION_OP_STAR, RIGHT_CURLY);
        testTokenization("${a[@]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);
        testTokenization("${a[*]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, RIGHT_CURLY);
    }

    @Test
    public void testArithmeticLiterals() throws Exception {
        testTokenization("$((123))", DOLLAR, EXPR_ARITH, NUMBER, _EXPR_ARITH);

        testTokenization("$((0x123))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((0xA))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((0xAf))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((-0xAf))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((--0xAf))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((+0xAf))", DOLLAR, EXPR_ARITH, ARITH_PLUS, ARITH_HEX_NUMBER, _EXPR_ARITH);

        testTokenization("$((10#1))", DOLLAR, EXPR_ARITH, ARITH_BASE_NUMBER, _EXPR_ARITH);
        testTokenization("$((10#100))", DOLLAR, EXPR_ARITH, ARITH_BASE_NUMBER, _EXPR_ARITH);
        testTokenization("$((-10#100))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_BASE_NUMBER, _EXPR_ARITH);
        testTokenization("$((+10#100))", DOLLAR, EXPR_ARITH, ARITH_PLUS, ARITH_BASE_NUMBER, _EXPR_ARITH);

        testTokenization("$((0123))", DOLLAR, EXPR_ARITH, ARITH_OCTAL_NUMBER, _EXPR_ARITH);
        testTokenization("$((-0123))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_OCTAL_NUMBER, _EXPR_ARITH);

        //also afe is not valid here we expect it to lex because we check the base in
        //an inspection
        testTokenization("$((10#100afe))", DOLLAR, EXPR_ARITH, ARITH_BASE_NUMBER, _EXPR_ARITH);

        testTokenization("$((35#abcdefghijkl))", DOLLAR, EXPR_ARITH, ARITH_BASE_NUMBER, _EXPR_ARITH);
    }


    private void testTokenization(String code, IElementType... expectedTokens) {
        testTokenization(BashVersion.Bash_v3, code, expectedTokens);
    }

    private void testTokenization(BashVersion version, String code, IElementType... expectedTokens) {
        BashLexer lexer = new BashLexer(version);
        lexer.start(code);

        int i = 1;
        StringBuffer data = new StringBuffer();
        for (IElementType expectedToken : expectedTokens) {
            IElementType tokenType = lexer.getTokenType();
            //data.append(tokenType.)
            Assert.assertEquals("Wrong match at #" + i,
                    expectedToken, tokenType);
            lexer.advance();
            ++i;
        }

        //check if the lexer has tokens left
        Assert.assertTrue("Lexer has tokens left: " + lexer.getTokenType(), lexer.getTokenType() == null);
    }
}
