/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: RedirectionParsing.java, Class: RedirectionParsing
 * Last modified: 2010-05-27
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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

import static com.ansorgit.plugins.bash.lang.parser.util.ParserUtil.error;

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

        bash 4 additional redirects:
          &<<
          >>& word
          >& word
     */
    private static final TokenSet validBeforeWord = TokenSet.create(
            GREATER_THAN, LESS_THAN, SHIFT_RIGHT, REDIRECT_LESS_LESS,
            REDIRECT_LESS_LESS_LESS, REDIRECT_LESS_LESS_MINUS,
            REDIRECT_LESS_GREATER, REDIRECT_GREATER_BAR,
            REDIRECT_AMP_GREATER_GREATER, REDIRECT_AMP_GREATER,
            REDIRECT_GREATER_AMP
    );

    private static final TokenSet validBeforeFiledescriptor = TokenSet.create(
            GREATER_THAN, LESS_THAN
    );

    private static final TokenSet heredocStarters = TokenSet.create(
            REDIRECT_LESS_LESS, REDIRECT_LESS_LESS_MINUS
    );

    public boolean parseList(BashPsiBuilder builder, boolean optional) {
        if (!isRedirect(builder)) {
            if (!optional) {
                error(builder, "parser.redirect.expected.notFound");
            }

            return optional;
        }

        final PsiBuilder.Marker redirectList = builder.mark();

        do {
            parseSingleRedirect(builder);
        } while (isRedirect(builder));

        redirectList.done(REDIRECT_LIST_ELEMENT);

        return true; //we had at least one redirect
    }

    //fixme profile and improve, if necessary. This implementation is not very smart at the moment.

    public boolean isRedirect(BashPsiBuilder builder) {
        if (builder.eof()) {
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();
        try {
            builder.enterNewErrorLevel(false);
            return parseSingleRedirect(builder, true);
        }
        finally {
            builder.leaveLastErrorLevel();
            marker.rollbackTo();
        }
    }

    public boolean parseSingleRedirect(BashPsiBuilder builder) {
        return parseSingleRedirect(builder, false);
    }

    public boolean parseSingleRedirect(BashPsiBuilder builder, boolean inCheckMode) {
        PsiBuilder.Marker marker = builder.mark();

        IElementType firstToken = builder.getTokenType();
        boolean firstIsInt = firstToken == INTEGER_LITERAL;

        //after a int as first token no whitespace may appear
        IElementType secondToken;
        if (firstIsInt) {
            builder.advanceLexer(true); //first token
            secondToken = builder.getTokenType(true);
        } else {
            secondToken = builder.getTokenType();
        }

        if (!redirectionSet.contains(secondToken)) {
            marker.drop();
            return false;
        }

        if (validBeforeFiledescriptor.contains(secondToken) && ParserUtil.hasNextTokens(builder, true, secondToken, FILEDESCRIPTOR)) {
            //avoid "advance without check" exceptions
            ParserUtil.getTokenAndAdvance(builder, true);

            PsiBuilder.Marker descriptorMarker = builder.mark();
            ParserUtil.getTokenAndAdvance(builder, true);
            descriptorMarker.done(FILEDESCRIPTOR);

            marker.done(REDIRECT_ELEMENT);
            return true;
        }

        //eat second token
        builder.advanceLexer();

        if (heredocStarters.contains(secondToken)) {
            if (inCheckMode) {
                marker.drop();
                return Parsing.word.isWordToken(builder);
            }

            if (!handleHereDocRedirect(builder)) {
                //marker.error("Expected heredoc marker");
                return false;
            }

            marker.done(REDIRECT_ELEMENT);
            return true;
        }

        boolean ok = Parsing.word.parseWord(builder);
        if (ok) {
            marker.done(REDIRECT_ELEMENT);
        } else {
            marker.drop();

            builder.error("Invalid redirect");
        }

        return ok;
    }

    private boolean handleHereDocRedirect(BashPsiBuilder builder) {
        if (!Parsing.word.isWordToken(builder)) {
            return false;
        }

        HereDocParsing.readHeredocMarker(builder);
        return true;
    }
}
