/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLexerTest.java, Class: BashLexerTest
 * Last modified: 2013-04-30
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
import org.junit.Ignore;
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
        testTokenization("$-", VARIABLE);
        testTokenization("$_", VARIABLE);
        testTokenization("$@", VARIABLE);
        testTokenization("$*", VARIABLE);
        testTokenization("$?", VARIABLE);

        testTokenization("$(echo)", DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN);
        testTokenization("${echo}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${_a}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${#echo}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, RIGHT_CURLY);
        testTokenization("${!echo}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, WORD, RIGHT_CURLY);
        testTokenization("a ${a# echo} a", WORD, WHITESPACE, DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_HASH, WHITESPACE, WORD, RIGHT_CURLY, WHITESPACE, WORD);
        testTokenization("${echo} ${echo}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, WHITESPACE, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("a=1 b=2 echo", ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, WORD);
        testTokenization("a=1 b=2", ASSIGNMENT_WORD, EQ, INTEGER_LITERAL, WHITESPACE, ASSIGNMENT_WORD, EQ, INTEGER_LITERAL);
        testTokenization("a+=a", ASSIGNMENT_WORD, ADD_EQ, WORD);
        testTokenization("if a; then PIDDIR=a$(a) a; fi", IF_KEYWORD, WHITESPACE, WORD, SEMI, WHITESPACE, THEN_KEYWORD, WHITESPACE, ASSIGNMENT_WORD, EQ, WORD, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, WHITESPACE, WORD, SEMI, WHITESPACE, FI_KEYWORD);

        //line continuation token is ignored
        testTokenization("a=a\\\nb", ASSIGNMENT_WORD, EQ, WORD, WORD);

        testTokenization("[ $(uname -a) ]", EXPR_CONDITIONAL, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, RIGHT_PAREN, _EXPR_CONDITIONAL);
    }

    @Test
    public void testArrayVariables() throws Exception {
        testTokenization("${PIPESTATUS[0]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${#myVar[*]}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${myVar[0]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("${myVar[*]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("${myVar[@]:1}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, PARAM_EXPANSION_OP_COLON, WORD, RIGHT_CURLY);

        testTokenization("a=( one two three)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_PAREN);

        testTokenization("a=( one two [2]=three)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WHITESPACE, WORD, WHITESPACE, WORD, WHITESPACE, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, EQ, WORD, RIGHT_PAREN);

        testTokenization("a=(1 2 3)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_PAREN);

        testTokenization("a[1]=", ASSIGNMENT_WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, EQ);
    }

    @Test
    public void testArrayWithString() throws Exception {
        // ARR=(['foo']='someval' ['bar']='otherval')

        testTokenization("a=(['x']=1)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, LEFT_SQUARE, STRING2, RIGHT_SQUARE, EQ, WORD, RIGHT_PAREN);
    }

    @Test
    public void testSquareBracketArithmeticExpr() {
        testTokenization("$[1]", DOLLAR, EXPR_ARITH_SQUARE, ARITH_NUMBER, _EXPR_ARITH_SQUARE);

        testTokenization("$[1 ]", DOLLAR, EXPR_ARITH_SQUARE, ARITH_NUMBER, WHITESPACE, _EXPR_ARITH_SQUARE);

        testTokenization("$[1/${a}]", DOLLAR, EXPR_ARITH_SQUARE, ARITH_NUMBER, ARITH_DIV, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_ARITH_SQUARE);

        //lexable, but bad syntax
        testTokenization("$(([1]))", DOLLAR, EXPR_ARITH, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, _EXPR_ARITH);
    }

    @Test
    public void testArithmeticExpr() {
        testTokenization("$((1))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((1,1))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, COMMA, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((1/${a}))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_DIV, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_ARITH);

        testTokenization("$((a=1,1))", DOLLAR, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, COMMA, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((((1))))", DOLLAR, EXPR_ARITH, LEFT_PAREN, LEFT_PAREN, ARITH_NUMBER, RIGHT_PAREN, RIGHT_PAREN, _EXPR_ARITH);

        testTokenization("$((-1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((--1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((--a))", DOLLAR, EXPR_ARITH, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((- --a))", DOLLAR, EXPR_ARITH, ARITH_MINUS, WHITESPACE, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((-1 -1))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_NUMBER, WHITESPACE, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a & b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, ARITH_BITWISE_AND, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((a && b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((a || b))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, _EXPR_ARITH);

        testTokenization("$((!a))", DOLLAR, EXPR_ARITH, ARITH_NEGATE, WORD, _EXPR_ARITH);

        testTokenization("$((~a))", DOLLAR, EXPR_ARITH, ARITH_BITWISE_NEGATE, WORD, _EXPR_ARITH);

        testTokenization("$((a>>2))", DOLLAR, EXPR_ARITH, WORD, ARITH_SHIFT_RIGHT, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a<<2))", DOLLAR, EXPR_ARITH, WORD, ARITH_SHIFT_LEFT, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a|2))", DOLLAR, EXPR_ARITH, WORD, PIPE, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a&2))", DOLLAR, EXPR_ARITH, WORD, ARITH_BITWISE_AND, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a^2))", DOLLAR, EXPR_ARITH, WORD, ARITH_BITWISE_XOR, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a%2))", DOLLAR, EXPR_ARITH, WORD, ARITH_MOD, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a-2))", DOLLAR, EXPR_ARITH, WORD, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a--))", DOLLAR, EXPR_ARITH, WORD, ARITH_MINUS_MINUS, _EXPR_ARITH);

        testTokenization("$((--a))", DOLLAR, EXPR_ARITH, ARITH_MINUS_MINUS, WORD, _EXPR_ARITH);

        testTokenization("$((a,2))", DOLLAR, EXPR_ARITH, WORD, COMMA, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a>2))", DOLLAR, EXPR_ARITH, WORD, ARITH_GT, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a > 2))", DOLLAR, EXPR_ARITH, WORD, WHITESPACE, ARITH_GT, WHITESPACE, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a>=2))", DOLLAR, EXPR_ARITH, WORD, ARITH_GE, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a<2))", DOLLAR, EXPR_ARITH, WORD, ARITH_LT, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((a<=2))", DOLLAR, EXPR_ARITH, WORD, ARITH_LE, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((1+-45))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((1+(-45)))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, LEFT_PAREN, ARITH_MINUS, ARITH_NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        testTokenization("$((1+---45))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_MINUS, ARITH_MINUS, ARITH_MINUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$(((1 << 10)))", DOLLAR, EXPR_ARITH, LEFT_PAREN, ARITH_NUMBER, WHITESPACE, ARITH_SHIFT_LEFT, WHITESPACE, ARITH_NUMBER, RIGHT_PAREN, _EXPR_ARITH);

        testTokenization("$((1 < \"1\"))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, WHITESPACE, ARITH_LT, WHITESPACE, STRING_BEGIN, WORD, STRING_END, _EXPR_ARITH);
    }

    @Ignore
    @Test
    public void testLetExpressions() throws Exception {
        //fixme unsure how the let expression should be tokenized. A solution might be to parse it as an lazy expression
        testTokenization("let a+=1", WORD, WHITESPACE, ASSIGNMENT_WORD, ARITH_ASS_PLUS, ARITH_NUMBER);
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
        testTokenization("\"a$\"", STRING_BEGIN, WORD, STRING_END);

        testTokenization("\"$(\"hey there\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(echo \\\"\\\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(echo \\\"\\\")\" a", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, RIGHT_PAREN, STRING_END, WHITESPACE, WORD);

        testTokenization("\"$(echo || echo)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(echo && echo)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(abc)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(1)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, INTEGER_LITERAL, RIGHT_PAREN, STRING_END);
        testTokenization("\"$((1))\"", STRING_BEGIN, DOLLAR, EXPR_ARITH, ARITH_NUMBER, _EXPR_ARITH, STRING_END);

        // "$("s/(/")" , the subshell command should be parsed as a word
        testTokenization("\"$(\"s/(/\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN, STRING_END);

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

        testTokenization("echo level%%[a-zA-Z]*", WORD, WHITESPACE, WORD);
        testTokenization("[ \\${a} ]", EXPR_CONDITIONAL, WORD, LEFT_CURLY, WORD, RIGHT_CURLY, _EXPR_CONDITIONAL);
        testTokenization("[  ]", EXPR_CONDITIONAL, _EXPR_CONDITIONAL);
        testTokenization("[ ]", EXPR_CONDITIONAL, _EXPR_CONDITIONAL);
        testTokenization("[ a  ]", EXPR_CONDITIONAL, WORD, WHITESPACE, _EXPR_CONDITIONAL);
        testTokenization("[ a | b ]", EXPR_CONDITIONAL, WORD, WHITESPACE, PIPE, WHITESPACE, WORD, _EXPR_CONDITIONAL);
        testTokenization("[[ a || b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization("${rdev%:*?}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, PARAM_EXPANSION_OP_COLON, PARAM_EXPANSION_OP_STAR, PARAM_EXPANSION_OP_QMARK, RIGHT_CURLY);
        testTokenization("${@!-+}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_AT, PARAM_EXPANSION_OP_EXCL, PARAM_EXPANSION_OP_MINUS, PARAM_EXPANSION_OP_PLUS, RIGHT_CURLY);
        testTokenization("${a[@]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);
        testTokenization("${\\ }", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);

        testTokenization("$\"\"", STRING_BEGIN, STRING_END);
        testTokenization("$\"abc\"", STRING_BEGIN, WORD, STRING_END);

        testTokenization("$''", STRING2);
        testTokenization("$'abc'", STRING2);

        testTokenization("\"(( $1 ))\"", STRING_BEGIN, WORD, VARIABLE, WORD, STRING_END);

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

        //no escape char here
        //fixme what is right here?
        //testTokenization("'$a\\' 'a'", STRING2, WHITESPACE, STRING2);
    }

    @Test
    public void testSubshellString() throws Exception {
        testTokenization("\"$( )\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WHITESPACE, RIGHT_PAREN, STRING_END);

        testTokenization("\"$( () )\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WHITESPACE, LEFT_PAREN,
                RIGHT_PAREN, WHITESPACE, RIGHT_PAREN, STRING_END);
    }

    @Test
    public void testWords() {
        testTokenization("%", WORD);
        testTokenization("a%b", WORD);
        testTokenization("a%b}", WORD, RIGHT_CURLY);
        testTokenization("echo level%%[a-zA-Z]*", WORD, WHITESPACE, WORD);
        testTokenization("tr [:upper:]", WORD, WHITESPACE, WORD);
        testTokenization("[!\"$2\"]", WORD, STRING_BEGIN, VARIABLE, STRING_END, WORD);

        testTokenization("unset todo_list[$todo_id]", WORD, WHITESPACE, ASSIGNMENT_WORD, LEFT_SQUARE, VARIABLE, RIGHT_SQUARE);
    }

    @Test
    public void testInternalCommands() {
        testTokenization("declare", WORD);
        testTokenization("echo", WORD);
        testTokenization("export", WORD);
        testTokenization("readonly", WORD);
        testTokenization("local", WORD);

        testTokenization(".", WORD);
        testTokenization(". /home/user/bashrc", WORD, WHITESPACE, WORD);
        testTokenization(". /home/user/bashrc$a", WORD, WHITESPACE, WORD, VARIABLE);
        testTokenization(". x >& x", WORD, WHITESPACE, WORD, WHITESPACE, REDIRECT_GREATER_AMP, WHITESPACE, WORD);

        testTokenization("''", STRING2);
        testTokenization("\"$('')\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, STRING2, RIGHT_PAREN, STRING_END);
        testTokenization("\"$('(')\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, STRING2, RIGHT_PAREN, STRING_END);

        testTokenization("\"$'(')\"", STRING_BEGIN, WORD, STRING_END);

        testTokenization("echo $", WORD, WHITESPACE, DOLLAR);
    }

    @Test
    public void testExpressions() {
        testTokenization("if [ -n \"a\" ]; then a; fi;",
                IF_KEYWORD, WHITESPACE, EXPR_CONDITIONAL, COND_OP, WHITESPACE, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, SEMI,
                WHITESPACE, THEN_KEYWORD, WHITESPACE, WORD, SEMI, WHITESPACE, FI_KEYWORD, SEMI);

        testTokenization("$((1+2))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, _EXPR_ARITH);
        testTokenization("((i=$(echo 1)))", EXPR_ARITH, ASSIGNMENT_WORD, EQ, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN, _EXPR_ARITH);
        testTokenization("((i=$((1 + 9))))",
                EXPR_ARITH, ASSIGNMENT_WORD, EQ, DOLLAR, EXPR_ARITH, ARITH_NUMBER, WHITESPACE,
                ARITH_PLUS, WHITESPACE, ARITH_NUMBER, _EXPR_ARITH, _EXPR_ARITH);

        testTokenization("((1 == 1 ? 0 : 0))",
                EXPR_ARITH, ARITH_NUMBER, WHITESPACE, ARITH_EQ, WHITESPACE, ARITH_NUMBER, WHITESPACE, ARITH_QMARK,
                WHITESPACE, ARITH_NUMBER, WHITESPACE, ARITH_COLON, WHITESPACE, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("a=(\na #this is a comment\nb)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, LINE_FEED, WORD, WHITESPACE, COMMENT, LINE_FEED, WORD, RIGHT_PAREN);
    }

    @Test
    public void testSubshell() {
        testTokenization("$(echo \"$1\")",
                DOLLAR, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, RIGHT_PAREN);
        testTokenization("$(($(echo \"$1\")))",
                DOLLAR, EXPR_ARITH, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, STRING_BEGIN, VARIABLE, STRING_END, RIGHT_PAREN, _EXPR_ARITH);
        testTokenization("`for d in`",
                BACKQUOTE, FOR_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, BACKQUOTE);
    }

    @Test
    public void testNumber() {
        testTokenization("123", INTEGER_LITERAL);
        testTokenization("$((123))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("123 456", INTEGER_LITERAL, WHITESPACE, INTEGER_LITERAL);
        testTokenization("$((123 234))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, WHITESPACE, ARITH_NUMBER, _EXPR_ARITH);
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
        testTokenization("${a%}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, RIGHT_CURLY);
        testTokenization("${a%b}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, WORD, RIGHT_CURLY);
        testTokenization("${#a}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, RIGHT_CURLY);
        testTokenization("${a1}", DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY);
        testTokenization("${/}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_SLASH, RIGHT_CURLY);
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

        testTokenization("[ 1 == 1 ]", EXPR_CONDITIONAL, WORD, WHITESPACE, COND_OP_EQ_EQ, WHITESPACE, WORD, _EXPR_CONDITIONAL);

        testTokenization("[ 1 =~ 1 ]", EXPR_CONDITIONAL, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _EXPR_CONDITIONAL);
    }

    @Test
    public void testBracket() {
        testTokenization("[[ -f test.txt ]]", BRACKET_KEYWORD, COND_OP, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization(" ]]", WHITESPACE, WORD);
        testTokenization("  ]]", WHITESPACE, WHITESPACE, WORD);
        testTokenization("[[  -f test.txt   ]]", BRACKET_KEYWORD, WHITESPACE, COND_OP, WHITESPACE, WORD, WHITESPACE, WHITESPACE, _BRACKET_KEYWORD);
        testTokenization("[[ !(a) ]]", BRACKET_KEYWORD, COND_OP_NOT, LEFT_PAREN, WORD, RIGHT_PAREN, _BRACKET_KEYWORD);

        testTokenization("[[ a && b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, AND_AND, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization("[[ a || b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, OR_OR, WHITESPACE, WORD, _BRACKET_KEYWORD);

        testTokenization("[[ -z \"\" ]]", BRACKET_KEYWORD, COND_OP, WHITESPACE, STRING_BEGIN, STRING_END, _BRACKET_KEYWORD);

        testTokenization("[[ a == b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_EQ_EQ, WHITESPACE, WORD, _BRACKET_KEYWORD);
        testTokenization("[[ a =~ b ]]", BRACKET_KEYWORD, WORD, WHITESPACE, COND_OP_REGEX, WHITESPACE, WORD, _BRACKET_KEYWORD);
    }

    @Test
    public void testParameterSubstitution() {
        testTokenization("${a=x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_EQ, WORD, RIGHT_CURLY);
        testTokenization("${a:=x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_EQ, WORD, RIGHT_CURLY);

        testTokenization("${a-x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, WORD, RIGHT_CURLY);
        testTokenization("${a:-x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_MINUS, WORD, RIGHT_CURLY);

        testTokenization("${a:?x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_QMARK, WORD, RIGHT_CURLY);

        testTokenization("${a+x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PLUS, WORD, RIGHT_CURLY);
        testTokenization("${a:+x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON_PLUS, WORD, RIGHT_CURLY);

        testTokenization("${!a}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_EXCL, WORD, RIGHT_CURLY);

        testTokenization("${a?x}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_QMARK, WORD, RIGHT_CURLY);

        testTokenization("${a-(none)}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, LEFT_PAREN, WORD, RIGHT_PAREN, RIGHT_CURLY);

        testTokenization("${@}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_AT, RIGHT_CURLY);

        testTokenization("${x/a//}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, WORD, PARAM_EXPANSION_OP_SLASH, PARAM_EXPANSION_OP_SLASH, RIGHT_CURLY);

        testTokenization("${x/,//}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_SLASH, WORD, PARAM_EXPANSION_OP_SLASH, PARAM_EXPANSION_OP_SLASH, RIGHT_CURLY);
    }

    @Test
    public void testWeirdStuff1() {
        testTokenization(": > a", WORD, WHITESPACE, GREATER_THAN, WHITESPACE, WORD);
        testTokenization("^", WORD);
        testTokenization("$!", VARIABLE);
        testTokenization("!", BANG_TOKEN);
        testTokenization("+m", WORD);
        testTokenization("\\#", WORD);
        testTokenization("a,b", WORD);
        testTokenization("~", WORD);
        testTokenization("a~", WORD);
        testTokenization("\\$", WORD);
        testTokenization("a[1]=2", ASSIGNMENT_WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, EQ, INTEGER_LITERAL);
        testTokenization("a[1]='2'", ASSIGNMENT_WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, EQ, STRING2);
        testTokenization("a[1+2]='2'", ASSIGNMENT_WORD, LEFT_SQUARE, ARITH_NUMBER, ARITH_PLUS, ARITH_NUMBER, RIGHT_SQUARE, EQ, STRING2);
        testTokenization("esac;", WORD, SEMI);

        //"$(echo "123")"
        testTokenization("\"$(echo 123)\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, WORD, WHITESPACE, INTEGER_LITERAL, RIGHT_PAREN, STRING_END);
        testTokenization("\"$(\"123\")\"", STRING_BEGIN, DOLLAR, LEFT_PAREN, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN, STRING_END);
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
        testTokenization("echo ${feld[${index}]}", WORD, WHITESPACE, DOLLAR, LEFT_CURLY,
                WORD, LEFT_SQUARE, DOLLAR, LEFT_CURLY, WORD, RIGHT_CURLY, RIGHT_SQUARE, RIGHT_CURLY);

        //shebang line after first line
        testTokenization("echo; #!/bin/sh", WORD, SEMI, WHITESPACE, SHEBANG);

        testTokenization("TOMCAT_HOST_LIST[$index]=$LINE", ASSIGNMENT_WORD, LEFT_SQUARE, VARIABLE, RIGHT_SQUARE, EQ, VARIABLE);

    }


    @Ignore
    @Test
    public void testUnsupported() throws Exception {

        //keyword as for loop variable
        //fixme currently unsupported, the case lexing is not context sensitive (hard to fix)
        testTokenization("for case in a; do\n" +
                "echo\n" +
                "done;", FOR_KEYWORD, WHITESPACE, CASE_KEYWORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, SEMI, WHITESPACE, DO_KEYWORD, LINE_FEED, WORD, LINE_FEED, DONE_KEYWORD, SEMI);
    }

    @Test
    public void testCaseWhitespacePattern() throws Exception {
        testTokenization("case x in\n" +
                "a\\ b)\n" +
                ";;\n" +
                "esac", CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, LINE_FEED, WORD, RIGHT_PAREN, LINE_FEED, CASE_END, LINE_FEED, ESAC_KEYWORD);

    }

    @Test
    public void testNestedCase() throws Exception {
        testTokenization("case x in x) ;; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, CASE_END, WHITESPACE, ESAC_KEYWORD);

        testTokenization("$(case x in x) ;; esac)",
                DOLLAR, LEFT_PAREN, CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, CASE_END, WHITESPACE, ESAC_KEYWORD, RIGHT_PAREN);
        testTokenization("(case x in x) ;; esac)",
                LEFT_PAREN, CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, CASE_END, WHITESPACE, ESAC_KEYWORD, RIGHT_PAREN);

        testTokenization("`case x in x) ;; esac `",
                BACKQUOTE, CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, CASE_END, WHITESPACE, ESAC_KEYWORD, WHITESPACE, BACKQUOTE);

        testTokenization("case x in esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, ESAC_KEYWORD);

        testTokenization("`case x in esac`;",
                BACKQUOTE, CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, ESAC_KEYWORD, BACKQUOTE, SEMI);

        testTokenization("case x in\n" +
                        "a\\ b)\n" +
                        "x=`case x in x) echo;; esac`\n" +
                        ";;\n" +
                        "esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, LINE_FEED,
                WORD, RIGHT_PAREN, LINE_FEED,
                ASSIGNMENT_WORD, EQ, BACKQUOTE, CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN, WHITESPACE, WORD, CASE_END, WHITESPACE, ESAC_KEYWORD, BACKQUOTE, LINE_FEED,
                CASE_END, LINE_FEED, ESAC_KEYWORD);

    }

    @Test
    public void testBackquote1() {
        testTokenization("`", BACKQUOTE);
        testTokenization("``", BACKQUOTE, BACKQUOTE);
        testTokenization("`echo a`", BACKQUOTE, WORD, WHITESPACE, WORD, BACKQUOTE);
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

        testTokenization("case a in \"a b\") echo a;; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, STRING_BEGIN, WORD, STRING_END, RIGHT_PAREN,
                WHITESPACE, WORD, WHITESPACE, WORD, CASE_END, WHITESPACE, ESAC_KEYWORD);

        //v3 vs. v4 changes in end marker
        testTokenization(BashVersion.Bash_v4, "case a in a);;& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, WHITESPACE, ESAC_KEYWORD);
        testTokenization(BashVersion.Bash_v3, "case a in a);;& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, AMP, WHITESPACE, ESAC_KEYWORD);

        //v3 vs. v4 changes in new end marker
        testTokenization(BashVersion.Bash_v4, "case a in a);& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, CASE_END, WHITESPACE, ESAC_KEYWORD);
        testTokenization(BashVersion.Bash_v3, "case a in a);& esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD,
                WHITESPACE, WORD, RIGHT_PAREN, SEMI, AMP, WHITESPACE, ESAC_KEYWORD);

        testTokenization("case a in a=a) echo a;; esac;",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, RIGHT_PAREN,
                WHITESPACE, WORD, WHITESPACE, WORD, CASE_END, WHITESPACE, ESAC_KEYWORD, SEMI);

    }

    @Test
    public void testAssignmentList() {
        testTokenization("a=(1)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, RIGHT_PAREN);
        testTokenization("a=(a b c)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, WHITESPACE, WORD, WHITESPACE, WORD, RIGHT_PAREN);
        testTokenization("a=(a,b,c)", ASSIGNMENT_WORD, EQ, LEFT_PAREN, WORD, RIGHT_PAREN);
    }

    @Test
    public void testEval() {
        testTokenization("eval [ \"a\" ]",
                WORD, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL);

        testTokenization("eval \"echo $\"",
                WORD, WHITESPACE, STRING_BEGIN, WORD, STRING_END);

        testTokenization("for f in a; do eval [ \"a\" ]; done",
                FOR_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD, SEMI,
                WHITESPACE, DO_KEYWORD, WHITESPACE,
                WORD, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL,
                SEMI, WHITESPACE, DONE_KEYWORD);

        testTokenization("case a in a) echo [ \"a\" ];; esac",
                CASE_KEYWORD, WHITESPACE, WORD, WHITESPACE, IN_KEYWORD, WHITESPACE, WORD,
                RIGHT_PAREN, WHITESPACE, WORD, WHITESPACE, EXPR_CONDITIONAL, STRING_BEGIN, WORD, STRING_END, _EXPR_CONDITIONAL, CASE_END, WHITESPACE, ESAC_KEYWORD);
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
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, SEMI,
                SEMI, _EXPR_ARITH);

        testTokenization("for ((a=1;a;))",
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, SEMI,
                WORD, SEMI, _EXPR_ARITH);

        testTokenization("for ((a=1;a<2;a=a+1))",
                FOR_KEYWORD, WHITESPACE, EXPR_ARITH, ASSIGNMENT_WORD, EQ, ARITH_NUMBER, SEMI,
                WORD, ARITH_LT, ARITH_NUMBER, SEMI, ASSIGNMENT_WORD, EQ, WORD, ARITH_PLUS, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$(read -p \"a\")",
                DOLLAR, LEFT_PAREN, WORD, WHITESPACE, WORD, WHITESPACE, STRING_BEGIN, WORD,
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
        testTokenization("${a:.*}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_COLON, PARAM_EXPANSION_OP_DOT, PARAM_EXPANSION_OP_STAR, RIGHT_CURLY);
        testTokenization("${a[@]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE, RIGHT_CURLY);
        testTokenization("${a[*]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${level%%[a-zA-Z]*}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_PERCENT, PARAM_EXPANSION_OP_PERCENT, LEFT_SQUARE, WORD, PARAM_EXPANSION_OP_MINUS, WORD, PARAM_EXPANSION_OP_MINUS, WORD, RIGHT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_CURLY);

        testTokenization("${a[var]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE, RIGHT_CURLY);
        testTokenization("${a[var+var2]}", DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, ARITH_PLUS, WORD, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${a[var+var2+1]#[a-z][0]}",
                DOLLAR, LEFT_CURLY, WORD, LEFT_SQUARE, WORD, ARITH_PLUS, WORD, ARITH_PLUS, ARITH_NUMBER, RIGHT_SQUARE,
                PARAM_EXPANSION_OP_HASH, LEFT_SQUARE, WORD, PARAM_EXPANSION_OP_MINUS, WORD, RIGHT_SQUARE,
                LEFT_SQUARE, WORD, RIGHT_SQUARE,
                RIGHT_CURLY);

        testTokenization("${#a[1]}", DOLLAR, LEFT_CURLY, PARAM_EXPANSION_OP_HASH, WORD, LEFT_SQUARE, ARITH_NUMBER, RIGHT_SQUARE, RIGHT_CURLY);

        testTokenization("${a-`$[1]`}", DOLLAR, LEFT_CURLY, WORD, PARAM_EXPANSION_OP_MINUS, BACKQUOTE, DOLLAR, EXPR_ARITH_SQUARE, ARITH_NUMBER, _EXPR_ARITH_SQUARE, BACKQUOTE, RIGHT_CURLY);
    }

    @Test
    public void testArithmeticLiterals() throws Exception {
        testTokenization("$((123))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((0x123))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((0xA))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((0xAf))", DOLLAR, EXPR_ARITH, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((-0xAf))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((--0xAf))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_MINUS, ARITH_HEX_NUMBER, _EXPR_ARITH);
        testTokenization("$((+0xAf))", DOLLAR, EXPR_ARITH, ARITH_PLUS, ARITH_HEX_NUMBER, _EXPR_ARITH);

        testTokenization("$((10#1))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_BASE_CHAR, ARITH_NUMBER, _EXPR_ARITH);
        testTokenization("$((10#100))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_BASE_CHAR, ARITH_NUMBER, _EXPR_ARITH);
        testTokenization("$((-10#100))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_NUMBER, ARITH_BASE_CHAR, ARITH_NUMBER, _EXPR_ARITH);
        testTokenization("$((+10#100))", DOLLAR, EXPR_ARITH, ARITH_PLUS, ARITH_NUMBER, ARITH_BASE_CHAR, ARITH_NUMBER, _EXPR_ARITH);

        testTokenization("$((0123))", DOLLAR, EXPR_ARITH, ARITH_OCTAL_NUMBER, _EXPR_ARITH);
        testTokenization("$((-0123))", DOLLAR, EXPR_ARITH, ARITH_MINUS, ARITH_OCTAL_NUMBER, _EXPR_ARITH);

        //also afe is not valid here we expect it to lex because we check the base in
        //an inspection
        testTokenization("$((10#100afe))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_BASE_CHAR, ARITH_NUMBER, WORD, _EXPR_ARITH);

        testTokenization("$((12#D))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_BASE_CHAR, WORD, _EXPR_ARITH);

        testTokenization("$((35#abcdefghijkl))", DOLLAR, EXPR_ARITH, ARITH_NUMBER, ARITH_BASE_CHAR, WORD, _EXPR_ARITH);
    }


    @Test
    public void testReadCommand() throws Exception {
        testTokenization("read \"var:\" v[i]", WORD, WHITESPACE, STRING_BEGIN, WORD, STRING_END, WHITESPACE, ASSIGNMENT_WORD, LEFT_SQUARE, WORD, RIGHT_SQUARE);
    }

    @Test
    public void testUmlaut() throws Exception {
        testTokenization("echo ä", WORD, WHITESPACE, WORD);
    }

    @Test
    public void testSubshellExpr() {
        testTokenization("`dd if=a`", BACKQUOTE, WORD, WHITESPACE, IF_KEYWORD, EQ, WORD, BACKQUOTE);
    }

    private void testTokenization(String code, IElementType... expectedTokens) {
        testTokenization(BashVersion.Bash_v3, code, expectedTokens);
    }

    private void testTokenization(BashVersion version, String code, IElementType... expectedTokens) {
        BashLexer lexer = new BashLexer(version);
        lexer.start(code);

        int i = 1;
        for (IElementType expectedToken : expectedTokens) {
            IElementType tokenType = lexer.getTokenType();
            Assert.assertEquals("Wrong match at #" + i,
                    expectedToken, tokenType);
            lexer.advance();
            ++i;
        }

        //check if the lexer has tokens left
        Assert.assertTrue("Lexer has tokens left: " + lexer.getTokenType(), lexer.getTokenType() == null);
    }
}
