/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParserUtil.java, Class: ParserUtil
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

package com.ansorgit.plugins.bash.lang.parser.util;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.util.BashStrings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.PropertyKey;

import java.util.List;
import java.util.Set;

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

    public static void error(BashPsiBuilder builder, @PropertyKey(resourceBundle = BUNDLE) String message) {
        builder.error(BashStrings.message(message));
    }

    public static void error(PsiBuilder.Marker marker, @PropertyKey(resourceBundle = BUNDLE) String message) {
        marker.error(BashStrings.message(message));
    }

    public static IElementType getTokenAndAdvance(BashPsiBuilder builder, boolean showWhitespace) {
        try {
            return builder.getTokenType(showWhitespace);
        }
        finally {
            if (!builder.eof()) {
                builder.advanceLexer();
            }
        }
    }

    public static IElementType getTokenAndAdvance(BashPsiBuilder builder) {
        return getTokenAndAdvance(builder, false);
    }

    public static List<IElementType> readAllOf(BashPsiBuilder builder, IElementType... types) {
        Set<IElementType> valid = Sets.newHashSet(types);
        List<IElementType> result = Lists.newArrayList();

        while (valid.contains(builder.getTokenType())) {
            result.add(getTokenAndAdvance(builder));
        }

        return result;
    }

    public static void markTokenAndAdvance(BashPsiBuilder builder, IElementType markAs) {
        final PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer();
        marker.done(markAs);
    }

    public static boolean checkNextAndRollback(BashPsiBuilder builder, IElementType... expected) {
        final PsiBuilder.Marker start = builder.mark();
        try {
            for (final IElementType expectedToken : expected) {
                final IElementType next = getTokenAndAdvance(builder);
                if (next != expectedToken) {
                    return false;
                }
            }
        } finally {
            start.rollbackTo();
        }

        return true;
    }

    public static boolean checkNextOrError(BashPsiBuilder builder, IElementType expected, @PropertyKey(resourceBundle = BUNDLE) String message) {
        final IElementType next = getTokenAndAdvance(builder);
        if (next != expected) {
            error(builder, message);
            return false;
        }

        return true;
    }

    public static boolean checkNextOrError(BashPsiBuilder builder, IElementType expected, @PropertyKey(resourceBundle = BUNDLE) String message, PsiBuilder.Marker marker) {
        final IElementType next = getTokenAndAdvance(builder);
        if (next != expected) {
            error(marker, message);
            return false;
        }

        return true;
    }

    public static boolean isWordToken(IElementType token) {
        //fixme add NUMBER?
        return BashTokenTypes.stringLiterals.contains(token);// || token == BashTokenTypes.VARIABLE;
    }

    public static boolean isIdentifier(IElementType tokenType) {
        return tokenType == BashTokenTypes.WORD;
    }

    private static final TokenSet simpleCommandEnds = TokenSet.create(
            BashTokenTypes.SEMI, BashTokenTypes.LINE_FEED,
            BashTokenTypes.AND_AND, BashTokenTypes.OR_OR
    );

    public static boolean isSimpleCommandEnd(IElementType tokenType) {
        return simpleCommandEnds.contains(tokenType);
    }

    public static boolean hasNextTokens(BashPsiBuilder builder, IElementType... tokens) {
        if (tokens.length == 1) return tokens[0] == builder.getTokenType();

        final PsiBuilder.Marker start = builder.mark();
        try {
            for (IElementType t : tokens) {
                if (t != builder.getTokenType()) return false;
                builder.advanceLexer();
            }

            return true;
        }
        finally {
            start.rollbackTo();
        }
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
