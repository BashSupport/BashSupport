/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ForLoopParsingFunction.java, Class: ForLoopParsingFunction
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 02.05.2009
 * Time: 11:21:19
 *
 * @author Joachim Ansorg
 */
public class ForLoopParsingFunction extends DefaultParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ForLoopParsingFunction");
    private final LoopParserUtil helper = new LoopParserUtil();

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == FOR_KEYWORD
                || isArithmeticForLoop(builder);
    }

    private boolean isArithmeticForLoop(BashPsiBuilder builder) {
        return ParserUtil.hasNextTokens(builder, FOR_KEYWORD, EXPR_ARITH);
    }

    public boolean isValid(IElementType token) {
        throw new UnsupportedOperationException("token is not supported");
    }

    public boolean parse(BashPsiBuilder builder) {
        /*
           for_command:
                FOR WORD newline_list DO compound_list DONE
             |    FOR WORD newline_list '{' compound_list '}'
             |    FOR WORD ';' newline_list DO compound_list DONE
             |    FOR WORD ';' newline_list '{' compound_list '}'
             |    FOR WORD newline_list IN word_list list_terminator newline_list DO compound_list DONE
             |    FOR WORD newline_list IN word_list list_terminator newline_list '{' compound_list '}'
             |    FOR WORD newline_list IN list_terminator newline_list DO compound_list DONE
             |    FOR WORD newline_list IN list_terminator newline_list '{' compound_list '}'
        */

        log.assertTrue(builder.getTokenType() == ShellCommandParsing.FOR_KEYWORD);

        if (isArithmeticForLoop(builder)) {
            return parseArithmeticForLoop(builder);
        }

        return parseForLoop(builder);
    }

    private boolean parseForLoop(BashPsiBuilder builder) {
        final PsiBuilder.Marker forLoop = builder.mark();
        builder.advanceLexer();//after the "for"

        //now just a single word
        if (ParserUtil.isIdentifier(builder.getTokenType())) {
            //mark the word as var
            ParserUtil.markTokenAndAdvance(builder, VAR_DEF_ELEMENT);
        } else {
            ParserUtil.error(forLoop, "parser.shell.for.expectedWord");
            return false;
        }

        builder.eatOptionalNewlines();

        //now either do, a block {} or IN
        final IElementType afterLoopValue = ParserUtil.getTokenAndAdvance(builder);
        if (afterLoopValue == ShellCommandParsing.DO_KEYWORD || afterLoopValue == ShellCommandParsing.LEFT_CURLY || afterLoopValue == ShellCommandParsing.SEMI) {
            if (afterLoopValue == ShellCommandParsing.SEMI) {
                builder.eatOptionalNewlines();
            }
        } else if (afterLoopValue == ShellCommandParsing.IN_KEYWORD) {
            //already after "in"

            //parse the optional word list
            if (!Parsing.word.parseWordList(builder, true, false)) {
                forLoop.drop();//fixme
                return false;
            }

            builder.eatOptionalNewlines();
        }

        if (!helper.parseLoopBody(builder, true)) {
            forLoop.drop();
            return false;
        }

        forLoop.done(ShellCommandParsing.FOR_COMMAND);
        return true;
    }

    /**
     * Helper function to parse an arithmetic for loop.
     *
     * @param builder The builder to use.
     * @return True if the arithmetic for loop has succesfully been parsed.
     */
    boolean parseArithmeticForLoop(BashPsiBuilder builder) {
        /*
            arith_for_command:
                    FOR ARITH_FOR_EXPRS list_terminator newline_list DO compound_list DONE
            |		FOR ARITH_FOR_EXPRS list_terminator newline_list '{' compound_list '}'
            |		FOR ARITH_FOR_EXPRS DO compound_list DONE
            |		FOR ARITH_FOR_EXPRS '{' compound_list '}'
            ;
         */

        if (log.isDebugEnabled()) log.assertTrue(builder.getTokenType() == FOR_KEYWORD);

        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();//after the "for" keyword

        //parse arithmetic expressions, it's a block like (( a; b; c )) with a,b,c
        //being arithmetic expressions
        if (builder.getTokenType() != EXPR_ARITH) {
            ParserUtil.error(marker, "parser.unexpected.token");
            return false;
        }

        builder.advanceLexer();//after the "((" token

        if (!parseArithmeticExpression(builder, SEMI)) {
            ParserUtil.error(marker, "parser.unexpected.token");
            return false;
        }

        if (!parseArithmeticExpression(builder, SEMI)) {
            ParserUtil.error(marker, "parser.unexpected.token");
            return false;
        }

        if (!parseArithmeticExpression(builder, _EXPR_ARITH)) {
            ParserUtil.error(marker, "parser.unexpected.token");
            return false;
        }

        if (Parsing.list.isListTerminator(builder.getTokenType())) {
            builder.advanceLexer();
            builder.eatOptionalNewlines();
        }

        if (!helper.parseLoopBody(builder, true)) {
            marker.drop();
            return false;
        }

        marker.done(ShellCommandParsing.FOR_COMMAND);
        return true;
    }


    /**
     * parses an optional arithmetic expression with an expected end token
     *
     * @param builder  The builder which provides the tokens.
     * @param endToken The token which is at the end of the arithmetic expression.
     * @return True if the parsing has been successfull.
     */
    private boolean parseArithmeticExpression(BashPsiBuilder builder, IElementType endToken) {
        if (builder.getTokenType() == endToken) {//the expression can be empty
            builder.advanceLexer();
            return true;
        }

        while ((builder.getTokenType() != endToken) && !builder.eof()) {
            if (!Parsing.shellCommand.arithmeticParser.readArithmeticPart(builder)) {
                return false;
            }
        }

        final IElementType foundEndToken = ParserUtil.getTokenAndAdvance(builder);
        return (!builder.eof() && foundEndToken == endToken);
    }
}
