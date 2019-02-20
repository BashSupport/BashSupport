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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticFactory;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for for loops statements.
 * <br>
 *
 * @author jansorg
 */
public class ForLoopParsingFunction implements ParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.ForLoopParsingFunction");
    private static final IElementType[] ARITH_FOR_LOOP_START = {FOR_KEYWORD, EXPR_ARITH};

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == FOR_KEYWORD;
    }

    private boolean isArithmeticForLoop(PsiBuilder builder) {
        return ParserUtil.hasNextTokens(builder, false, ARITH_FOR_LOOP_START);
    }

    public boolean parse(BashPsiBuilder builder) {
        /* The grammar:

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

        return isArithmeticForLoop(builder)
                ? parseArithmeticForLoop(builder)
                : parseForLoop(builder);
    }

    private boolean parseForLoop(BashPsiBuilder builder) {
        final PsiBuilder.Marker forLoop = builder.mark();
        builder.advanceLexer();//after the "for"

        //now just a single word
        if (ParserUtil.isIdentifier(builder.getTokenType())) {
            //mark the word as var
            ParserUtil.remapMarkAdvance(builder, WORD, VAR_DEF_ELEMENT);
        } else {
            forLoop.drop();
            ParserUtil.error(builder, "parser.shell.for.expectedWord");
            return false;
        }

        builder.readOptionalNewlines();

        //now either do, a block {} or IN
        final IElementType afterLoopValue = builder.getTokenType();
        if (afterLoopValue == SEMI) {
            builder.advanceLexer();
            builder.readOptionalNewlines();
        } else if ((afterLoopValue == WORD || afterLoopValue == IN_KEYWORD_REMAPPED) && "in".equals(builder.getTokenText())) {
            builder.remapCurrentToken(IN_KEYWORD_REMAPPED);
            builder.advanceLexer(); //in keyword

            //parse the optional word list
            if (builder.getTokenType() == SEMI) {
                builder.advanceLexer();
            } else if (!Parsing.word.parseWordListIfValid(builder, true, false).isParsedSuccessfully()) {//fixme validate
                forLoop.drop();//fixme
                return false;
            }

            builder.readOptionalNewlines();
        }

        if (!LoopParserUtil.parseLoopBody(builder, true, false)) {
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
            builder.readOptionalNewlines();
        }

        if (!LoopParserUtil.parseLoopBody(builder, true, false)) {
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
            if (!ArithmeticFactory.entryPoint().parse(builder)) {
                return false;
            }
        }

        final IElementType foundEndToken = ParserUtil.getTokenAndAdvance(builder);
        return (!builder.eof() && foundEndToken == endToken);
    }
}
