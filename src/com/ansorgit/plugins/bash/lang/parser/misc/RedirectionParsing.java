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

/**
 * @author jansorg
 */
//fixme refactor this
public class RedirectionParsing implements ParsingTool {
    private static final TokenSet validBeforeFiledescriptor = TokenSet.create(
            GREATER_THAN, LESS_THAN
    );

    //fixme profile and improve, if necessary. This implementation is not very smart at the moment.
    //fixme optimize this for performance
    public boolean isRedirect(BashPsiBuilder builder, boolean allowHeredocs) {
        if (builder.eof()) {
            return false;
        }

        if (isProcessSubstitution(builder)) {
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();
        builder.enterNewErrorLevel(false);

        boolean result = parseSingleRedirectIfValid(builder, allowHeredocs) == RedirectParseResult.OK;

        builder.leaveLastErrorLevel();

        marker.rollbackTo();
        return result;
    }

    public RedirectParseResult parseRequiredListIfValid(BashPsiBuilder builder, boolean allowHeredocs) {
        RedirectParseResult result = parseListIfValid(builder, allowHeredocs);
        if (result == RedirectParseResult.NO_REDIRECT) {
            builder.error("Missing redirect");
        }
        return result;
    }

    public RedirectParseResult parseListIfValid(BashPsiBuilder builder, boolean allowHeredocs) {
        int count = 0;
        PsiBuilder.Marker redirectList = builder.mark();

        RedirectParseResult result;
        do {
            result = parseSingleRedirectIfValid(builder, allowHeredocs);
            if (result == RedirectParseResult.NO_REDIRECT) {
                break;
            }
            if (result == RedirectParseResult.INVALID_REDIRECT) {
                builder.error("Invalid redirect");
                if (builder.getTokenType() != LINE_FEED) {
                    builder.advanceLexer();
                }
            } else if (result == RedirectParseResult.PARSING_FAILED) {
                builder.error("Invalid redirect");
                if (builder.getTokenType() != LINE_FEED) {
                    builder.advanceLexer();
                }
            }
            count++;
        } while (result == RedirectParseResult.OK);

        if (count == 0) {
            redirectList.drop();
            return result;
        }

        redirectList.done(REDIRECT_LIST_ELEMENT);

        // valid redirects before no more was found or at least one valid redirect found
        return result == RedirectParseResult.NO_REDIRECT ? RedirectParseResult.OK : result;
    }

    public RedirectParseResult parseSingleRedirectIfValid(BashPsiBuilder builder, boolean allowHeredoc) {
        if (isProcessSubstitution(builder)) {
            return RedirectParseResult.NO_REDIRECT;
        }

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
            marker.rollbackTo();
            return RedirectParseResult.NO_REDIRECT;
        }

        if (validBeforeFiledescriptor.contains(secondToken) && ParserUtil.hasNextTokens(builder, true, secondToken, FILEDESCRIPTOR)) {
            //avoid "advance without check" exceptions
            ParserUtil.getTokenAndAdvance(builder, true);

            PsiBuilder.Marker descriptorMarker = builder.mark();
            ParserUtil.getTokenAndAdvance(builder, true);
            descriptorMarker.done(FILEDESCRIPTOR);

            marker.done(REDIRECT_ELEMENT);
            return RedirectParseResult.OK;
        }

        //eat second token
        builder.advanceLexer();

        if (allowHeredoc && secondToken == HEREDOC_MARKER_TAG) {
            marker.drop();

            if (builder.getTokenType() == LINE_FEED) {
                // missing start tag
                builder.error("missing heredoc start tag");
                builder.advanceLexer();
                return RedirectParseResult.OK;
            }

            if (builder.getTokenType() != HEREDOC_MARKER_START) {
                return RedirectParseResult.PARSING_FAILED;
            }

            ParserUtil.markTokenAndAdvance(builder, HEREDOC_START_ELEMENT);
            builder.getParsingState().pushHeredocMarker(builder.rawTokenIndex());
            return RedirectParseResult.OK;
        }

        //read optional white space before the actual redirection target
        boolean ok = Parsing.word.parseWordIfValid(builder).isParsedSuccessfully();
        if (ok) {
            marker.done(REDIRECT_ELEMENT);
            return RedirectParseResult.OK;
        } else {
            //an invalid redirect should not break the whole parsing, thus we return true here
            marker.drop();
            return RedirectParseResult.INVALID_REDIRECT;
        }
    }

    private boolean isProcessSubstitution(BashPsiBuilder builder) {
        //avoid to parse a process substitution as a redirect expression
        int i = 0;
        while (builder.rawLookup(i) == WHITESPACE) {
            i++;
        }

        IElementType lookAhead = builder.rawLookup(i);
        return (lookAhead == LESS_THAN || lookAhead == GREATER_THAN) && builder.rawLookup(i + 1) == LEFT_PAREN;
    }


    public enum RedirectParseResult {
        NO_REDIRECT,
        INVALID_REDIRECT,
        PARSING_FAILED,
        OK;
    }
}
