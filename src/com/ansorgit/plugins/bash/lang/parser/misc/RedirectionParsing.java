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
 * @author jansorg
 */
//fixme refactor this
public class RedirectionParsing implements ParsingTool {
    private static final TokenSet validBeforeFiledescriptor = TokenSet.create(
            GREATER_THAN, LESS_THAN
    );

    private static final TokenSet heredocStarters = TokenSet.create(
            HEREDOC_MARKER_TAG
    );

    public boolean parseList(BashPsiBuilder builder, boolean optional, boolean allowHeredocs) {
        if (!isRedirect(builder, true)) {
            if (!optional) {
                error(builder, "parser.redirect.expected.notFound");
            }

            return optional;
        }

        PsiBuilder.Marker redirectList = builder.mark();

        do {
            parseSingleRedirect(builder, allowHeredocs);
        } while (isRedirect(builder, allowHeredocs));

        redirectList.done(REDIRECT_LIST_ELEMENT);

        return true; //we had at least one redirect
    }

    //fixme profile and improve, if necessary. This implementation is not very smart at the moment.
    //fixme optimize this for performance
    public boolean isRedirect(BashPsiBuilder builder, boolean allowHeredocs) {
        if (builder.eof()) {
            return false;
        }

        //avoid to parse a process substitution as a redirect expression
        int i = 0;
        while (builder.rawLookup(i) == WHITESPACE) {
            i++;
        }

        IElementType lookAhead = builder.rawLookup(i);
        if ((lookAhead == LESS_THAN || lookAhead == GREATER_THAN) && builder.rawLookup(i+1) == LEFT_PAREN){
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();
        builder.enterNewErrorLevel(false);

        boolean result = parseSingleRedirect(builder, true, allowHeredocs);

        builder.leaveLastErrorLevel();

        marker.rollbackTo();
        return result;
    }

    public boolean parseSingleRedirect(BashPsiBuilder builder, boolean allowHeredoc) {
        return parseSingleRedirect(builder, false, allowHeredoc);
    }

    private boolean parseSingleRedirect(BashPsiBuilder builder, boolean inCheckMode, boolean allowHeredoc) {
        PsiBuilder.Marker marker = builder.mark();

        IElementType firstToken = builder.getTokenType();
        boolean firstIsInt = firstToken == INTEGER_LITERAL;

        //after a int as first token no whitespace may appear
        IElementType secondToken;
        if (firstIsInt) {
            builder.advanceLexer(); //first token
            secondToken = builder.rawLookup(0);
        } else {
            //same as firstToken because there is no number before the redirect token
            secondToken = firstToken;
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

        if (allowHeredoc && heredocStarters.contains(secondToken)) {
            if (inCheckMode) {
                marker.drop();

                return builder.getTokenType() == HEREDOC_MARKER_START;
            }

            if (builder.getTokenType() != HEREDOC_MARKER_START) {
                marker.drop();
                return false;
            }

            marker.drop();

            ParserUtil.markTokenAndAdvance(builder, HEREDOC_START_ELEMENT);
            
            builder.getParsingState().pushHeredocMarker(builder.rawTokenIndex());
            
            return true;
        }

        //read optional white space before the actual redirection target
        boolean ok = Parsing.word.parseWordIfValid(builder).isParsedSuccessfully();
        if (ok) {
            marker.done(REDIRECT_ELEMENT);
        } else {
            marker.drop();

            //we try to avoid further error marks here
            builder.error("Invalid redirect");
            if (builder.getTokenType() != LINE_FEED) {
                builder.advanceLexer();
            }
        }

        //an invalid redirect should not break the whole parsing, thus we return true here
        return true;
    }
}
