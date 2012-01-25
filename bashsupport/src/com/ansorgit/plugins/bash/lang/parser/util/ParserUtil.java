/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ParserUtil.java, Class: ParserUtil
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

package com.ansorgit.plugins.bash.lang.parser.util;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.google.common.base.Function;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

/**
 * Useful helper methods for the language parsing.
 * <p/>
 * Date: 24.03.2009
 * Time: 21:22:30
 *
 * @author Joachim Ansorg
 */
public class ParserUtil {
    @NonNls
    private static final String BUNDLE = "com.ansorgit.plugins.bash.bash";

    public static void errorToken(PsiBuilder builder, @PropertyKey(resourceBundle = BUNDLE) String message) {
        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.error(BashStrings.message(message));
    }

    public static void error(PsiBuilder builder, @PropertyKey(resourceBundle = BUNDLE) String message) {
        builder.error(BashStrings.message(message));
    }

    public static void error(PsiBuilder.Marker marker, @PropertyKey(resourceBundle = BUNDLE) String message) {
        marker.error(BashStrings.message(message));
    }

    /**
     * Takes a token, optionally enabling the whitespace mode, advances if the builder is not yet
     * at the end and returns the previously taken token.
     *
     * @param builder        The token provider
     * @param showWhitespace If true whitespace tokens will be returned, too.
     * @return
     */
    public static IElementType getTokenAndAdvance(BashPsiBuilder builder, boolean showWhitespace) {
        try {
            return builder.getTokenType(showWhitespace);
        } finally {
            if (!builder.eof()) {
                builder.advanceLexer();
            }
        }
    }

    /**
     * Same as {@link com.ansorgit.plugins.bash.lang.parser.util.ParserUtil#getTokenAndAdvance(com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder, boolean)}
     * but always disables the whitespace mode.
     *
     * @param builder Provides the tokens.
     * @return The current token.
     */
    public static IElementType getTokenAndAdvance(BashPsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        //no EOF
        if (tokenType != null) {
            builder.advanceLexer();
        }

        return tokenType;
    }

    /**
     * Encloses the current token in a marker (the marker token is the 2nd paramter).
     *
     * @param builder Provides the tokens.
     * @param markAs  The type for the marker
     */
    public static void markTokenAndAdvance(PsiBuilder builder, IElementType markAs) {
        final PsiBuilder.Marker marker = builder.mark();

        builder.advanceLexer();

        marker.done(markAs);
    }

    /**
     * Turns off error reporting, then calls the function with the psi builder and then rolls back to the initial token position.
     * The result of the function is return as result of this call.
     *
     * @param builder
     * @param function
     * @return
     */
    public static boolean checkAndRollback(BashPsiBuilder builder, Function<BashPsiBuilder, Boolean> function) {
        final PsiBuilder.Marker start = builder.mark();
        try {
            builder.enterNewErrorLevel(false);
            return function.apply(builder);
        } finally {
            builder.leaveLastErrorLevel();
            start.rollbackTo();
        }
    }

    /**
     * Advances to the next token if the current token is of the specified type.
     *
     * @param builder To read from
     * @param token   The token to check for
     * @return True if the token has been red.
     */
    public static boolean conditionalRead(PsiBuilder builder, IElementType token) {
        if (builder.getTokenType() == token) {
            builder.advanceLexer();
            return true;
        }

        return false;
    }

    /**
     * Advances to the next token if the current token is of the specified type.
     *
     * @param builder To read from
     * @param tokens
     * @return True if the token has been red.
     */
    public static boolean conditionalRead(PsiBuilder builder, TokenSet tokens) {
        if (tokens.contains(builder.getTokenType())) {
            builder.advanceLexer();
            return true;
        }

        return false;
    }

    public static boolean checkNextOrError(BashPsiBuilder builder, PsiBuilder.Marker marker, IElementType expected, @PropertyKey(resourceBundle = BUNDLE) String message) {
        final IElementType next = getTokenAndAdvance(builder);
        if (next != expected) {
            marker.drop();
            error(builder, message);
            return false;
        }

        return true;
    }

    /**
     * Returns whether the provided token is a word token.
     *
     * @param token The token to check
     * @return True if the token is a valid word token.
     */
    public static boolean isWordToken(IElementType token) {
        return BashTokenTypes.stringLiterals.contains(token);
    }

    /**
     * Checks whether a token is a valid identifier.
     *
     * @param tokenType The token to check
     * @return True if the provided token is a valid identifier token.
     */
    public static boolean isIdentifier(IElementType tokenType) {
        return tokenType == BashTokenTypes.WORD;
    }

    public static boolean hasNextTokens(BashPsiBuilder builder, boolean enableWhitespace, IElementType... tokens) {
        for (int i = 0, tokensLength = tokens.length; i < tokensLength; i++) {
            IElementType lookAheadToken = enableWhitespace ? builder.rawLookup(i) : builder.lookAhead(i);
            if (lookAheadToken != tokens[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns whether the token can be seen as whitespace (i.e. either space or line feed).
     *
     * @param token The token to check.
     * @return True if token is a space or newline.
     */
    public static boolean isWhitespace(IElementType token) {
        return (token == BashTokenTypes.WHITESPACE) || (token == BashTokenTypes.LINE_FEED);
    }
}
