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

package com.ansorgit.plugins.bash.lang.parser.util;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.function.Predicate;

/**
 * Useful helper methods for the language parsing.
 * <br>
 * @author jansorg
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
        IElementType tokenType = builder.getTokenType(showWhitespace);

        if (!builder.eof()) {
            builder.advanceLexer();
        }

        return tokenType;
    }

    /**
     * Same as {@link com.ansorgit.plugins.bash.lang.parser.util.ParserUtil#getTokenAndAdvance(com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder, boolean)}
     * but always disables the whitespace mode.
     *
     * @param builder Provides the tokens.
     * @return The current token.
     */
    public static IElementType getTokenAndAdvance(PsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();

        //no EOF
        if (tokenType != null) {
            builder.advanceLexer();
        }

        return tokenType;
    }

    public static boolean smartRemapAndAdvance(PsiBuilder builder, String expectedTokenText, IElementType expectedTokenType, IElementType newTokenType) {
        IElementType current = builder.getTokenType();
        if (current == newTokenType) {
            // already remapped, probably due to reverting an earlier parse result
            builder.advanceLexer();
        } else if (expectedTokenText.equals(builder.getTokenText()) && current == expectedTokenType) {
            builder.remapCurrentToken(newTokenType);
            builder.advanceLexer();
        } else {
            builder.error("unexpected token");
            return false;
        }

        return true;
    }

    public static void remapMarkAdvance(PsiBuilder builder, IElementType newTokenType, IElementType markAs) {
        builder.remapCurrentToken(newTokenType);

        markTokenAndAdvance(builder, markAs);
    }

    /**
     * Encloses the current token in a marker (the marker token is the 2nd parameter).
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
    public static boolean checkAndRollback(BashPsiBuilder builder, Predicate<BashPsiBuilder> function) {
        final PsiBuilder.Marker start = builder.mark();
        builder.enterNewErrorLevel(false);

        boolean result = function.test(builder);

        builder.leaveLastErrorLevel();
        start.rollbackTo();

        return result;
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
        return tokenType == BashTokenTypes.WORD || BashTokenTypes.identifierKeywords.contains(tokenType);
    }

    public static boolean hasNextTokens(PsiBuilder builder, boolean enableWhitespace, IElementType... tokens) {
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
    public static boolean isWhitespaceOrLineFeed(IElementType token) {
        return (token == BashTokenTypes.WHITESPACE) || (token == BashTokenTypes.LINE_FEED);
    }

    public static boolean containsTokenInLookahead(PsiBuilder builder, IElementType token, int maxLookahead, boolean allowWhitespace) {
        int i = 0;

        while (i < maxLookahead) {
            IElementType current = allowWhitespace ? builder.lookAhead(i) : builder.rawLookup(i);
            if (current == null) {
                return false;
            }

            if (current == token) {
                return true;
            }

            i++;
        }

        return false;
    }

    /**
    public static boolean containsTokenInLookahead(PsiBuilder builder, TokenSet tokens, int maxLookahead, boolean allowWhitespace) {
        int i = 0;

        while (i < maxLookahead) {
            IElementType current = allowWhitespace ? builder.lookAhead(i) : builder.rawLookup(i);
            if (current == null) {
                return false;
            }

            if (tokens.contains(current)) {
                return true;
            }

            i++;
        }

        return false;
    }

     * Returns true if the next tokens are 0..n newlines followed by an optional semicolon followed by the given token
     * This is useful to detect empty commands in if/while/...
     *
     * @param token The token to check
     * @return true if an empty list of commands is followed by the token
     */
    public static boolean isEmptyListFollowedBy(BashPsiBuilder builder, IElementType token) {
        return isEmptyListFollowedBy(builder, TokenSet.create(token));
    }

    /**
     * Returns true if the next tokens are 0..n newlines followed by an optional semicolon followed by the given token
     * This is useful to detect empty commands in if/while/...
     *
     * @param tokens The tokens to check
     * @return true if an empty list of commands is followed by the token
     */
    public static boolean isEmptyListFollowedBy(BashPsiBuilder builder, TokenSet tokens) {
        if (tokens.contains(builder.getTokenType())) {
            return true;
        }

        int steps = 0;

        while (builder.lookAhead(steps) == BashTokenTypes.LINE_FEED) {
            steps++;
        }

        if (builder.lookAhead(steps) == BashTokenTypes.SEMI) {
            steps++;
        }

        while (builder.lookAhead(steps) == BashTokenTypes.LINE_FEED) {
            steps++;
        }

        return tokens.contains(builder.lookAhead(steps));
    }

    /**
     * Returns true if the next tokens are 0..n newlines followed by an optional semicolon followed by the given token
     * This is useful to detect empty commands in if/while/...
     *
     * @param token The token to check
     * @return true if an empty list of commands is followed by the token
     */
    public static boolean readEmptyListFollowedBy(BashPsiBuilder builder, IElementType token) {
        return readEmptyListFollowedBy(builder, TokenSet.create(token));
    }

    /**
     * Returns true if the next tokens are 0..n newlines followed by an optional semicolon followed by the given token
     * This is useful to detect empty commands in if/while/...
     *
     * @param tokens The tokens to check
     * @return true if an empty list of commands is followed by the token
     */
    public static boolean readEmptyListFollowedBy(BashPsiBuilder builder, TokenSet tokens) {
        if (tokens.contains(builder.getTokenType())) {
            return true;
        }

        int steps = 0;

        while (builder.lookAhead(steps) == BashTokenTypes.LINE_FEED) {
            steps++;
        }

        if (builder.lookAhead(steps) == BashTokenTypes.SEMI) {
            steps++;
        }

        while (builder.lookAhead(steps) == BashTokenTypes.LINE_FEED) {
            steps++;
        }

        if (tokens.contains(builder.lookAhead(steps))) {
            for (int i = 0; i < steps; i++) {
                builder.advanceLexer();
                builder.getTokenType();
            }

            return true;
        }

        return false;
    }

    /**
     * @param builder The PSI builder
     * @param text The string to match against the token text
     * @return {@code true} if the current token is a WORD and its token text is matching the given parameter value.
     */
    public static boolean isWord(@NotNull BashPsiBuilder builder, @NotNull String text) {
        return builder.getTokenType() == BashTokenTypes.WORD && text.equals(builder.getTokenText());
    }
}
