/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: RedirectionParsing.java, Class: RedirectionParsing
 * Last modified: 2010-01-29
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static com.ansorgit.plugins.bash.lang.parser.util.ParserUtil.error;
import static com.ansorgit.plugins.bash.lang.parser.util.ParserUtil.getTokenAndAdvance;

/**
 * Date: 24.03.2009
 * Time: 20:52:54
 *
 * @author Joachim Ansorg
 */
//fixme refactor this
public class RedirectionParsing implements ParsingTool {
    /*
     redirection	:
         | number? '>' word
         | number? '<' word
         | number? GREATER_GREATER word
         | number? LESS_LESS word
         | number? LESS_LESS_LESS word
         | number? GREATER_AND (number | word | '-')
         | number? LESS_AND (word|number | '-')
         | number? LESS_LESS_MINUS word
         | number? LESS_GREATER word
         | number? GREATER_BAR word
         ;

        bash 4 additional redirects: &<<
          >>& word
     */
    private static final TokenSet validBeforeWord = TokenSet.create(
            GREATER_THAN, LESS_THAN, SHIFT_RIGHT, REDIRECT_LESS_LESS,
            REDIRECT_LESS_LESS_LESS, REDIRECT_GREATER_AND, REDIRECT_LESS_AND,
            REDIRECT_LESS_LESS_MINUS, REDIRECT_LESS_GREATER, REDIRECT_GREATER_BAR,
            REDIRECT_AMP_GREATER_GREATER
    );

    private static final TokenSet validBeforeNumber = TokenSet.create(
            REDIRECT_GREATER_AND, REDIRECT_LESS_AND
    );

    private static TokenSet validBeforeMinus = TokenSet.create(
            REDIRECT_GREATER_AND, REDIRECT_LESS_AND
    );

    public boolean parseList(BashPsiBuilder builder, boolean optional) {
        if (!isRedirect(builder)) {
            if (!optional) {
                error(builder, "parser.redirect.expected.notFound");
            }

            return false;
        }

        final PsiBuilder.Marker redirectList = builder.mark();

        while (isRedirect(builder)) {
            parseSingleRedirect(builder);
        }

        redirectList.done(REDIRECT_LIST_ELEMENT);

        return true; //we had at least one redirect
    }

    public boolean isRedirect(BashPsiBuilder builder) {
        final PsiBuilder.Marker start = builder.mark();
        try {
            final boolean firstIsNumber = builder.getTokenType() == NUMBER;
            if (firstIsNumber) builder.advanceLexer();

            final IElementType redirectToken = ParserUtil.getTokenAndAdvance(builder);
            if (!BashTokenTypes.redirectionSet.contains(redirectToken)) {
                return false;
            }

            final boolean targetIsNumber = builder.getTokenType() == NUMBER;
            if (targetIsNumber) return validBeforeNumber.contains(redirectToken);

            final boolean targetIsWord = Parsing.word.isWordToken(builder);
            if (targetIsWord) return validBeforeWord.contains(redirectToken);
        } finally {
            start.rollbackTo();
        }

        return false;
    }

    /**
     * Parses the grammer of redirect expressions.
     * ENTRY Before the redirect expression
     * EXIT After the expression if found
     *
     * @param builder
     */
    public boolean parseSingleRedirect(BashPsiBuilder builder) {
        final IElementType firstToken = builder.getTokenType();

        final PsiBuilder.Marker redirect = builder.mark();

        if (firstToken == NUMBER) {
            builder.advanceLexer();
        }

        final IElementType secondToken = getTokenAndAdvance(builder);
        final IElementType thirdToken = builder.getTokenType();//not advancing

        //special handling for here document
        if (secondToken == REDIRECT_LESS_LESS || secondToken == REDIRECT_LESS_LESS_MINUS) {
            //boolean firstIsStart = firstToken == REDIRECT_LESS_LESS;
            //here doc
            if (!Parsing.word.isWordToken(builder)) {
                error(redirect, "parser.redirect.expected.string");
                return false;
            }

            //fixme better imple with a sort of capture mode?

            //get the name of the expected here doc end
            HereDocParsing.readHeredocMarker(builder);
            redirect.done(REDIRECT_ELEMENT);

            return true;
        }

        if (thirdToken != NUMBER && !Parsing.word.isWordToken(builder)) {
            return false;
        }

        //fixme still right?
        if (thirdToken == NUMBER && !validBeforeNumber.contains(secondToken)) {
            error(redirect, "parser.redirect.expected.filename");
            return false;
        } else if (thirdToken == NUMBER) {
            builder.advanceLexer();//read in the number
        }

        if (Parsing.word.isWordToken(builder) && validBeforeWord.contains(secondToken)) {
            //read the word (might be $() or something similair
            Parsing.word.parseWord(builder);
        } else {
            error(redirect, "parser.redirected.expteced.filedescriptor");
            return false;
        }

        //finish
        redirect.done(REDIRECT_ELEMENT);
        return true;
    }
}
