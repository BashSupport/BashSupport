/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: WordParsing.java, Class: WordParsing
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing of tokens which can be understood as word tokens, e.g. WORD, variables, subshell commands, etc.
 * <p/>
 * Date: 26.03.2009
 * Time: 15:55:24
 *
 * @author Joachim Ansorg
 */
public class WordParsing implements ParsingTool {
    private static final TokenSet singleDollarFollowups = TokenSet.create(STRING_END, WHITESPACE, LINE_FEED);

    /**
     * Checks whether the next tokens might belong to a word token.
     * The upcoming tokens are not remapped.
     *
     * @param builder Provides the tokens.
     * @return True if the next token is a word token.
     */
    public boolean isWordToken(final BashPsiBuilder builder) {
        return isWordToken(builder, false);
    }

    public boolean isWordToken(final BashPsiBuilder builder, final boolean enableRemapping) {
        final IElementType tokenType = builder.getTokenType(false, enableRemapping);
        boolean isWord = isComposedString(tokenType)
                || Parsing.braceExpansionParsing.isValid(builder)
                || BashTokenTypes.stringLiterals.contains(tokenType)
                || Parsing.var.isValid(builder)
                || Parsing.shellCommand.backtickParser.isValid(builder)
                || Parsing.shellCommand.conditionalExpressionParser.isValid(builder)
                || Parsing.processSubstitutionParsing.isValid(builder)
                || tokenType == LEFT_CURLY;

        if (isWord) {
            return true;
        }

        if (tokenType == DOLLAR) {
            PsiBuilder.Marker marker = builder.mark();
            try {
                builder.advanceLexer();
                IElementType next = builder.getTokenType();
                return singleDollarFollowups.contains(next);
            } finally {
                marker.rollbackTo();
            }
        }

        return false;
    }

    public boolean isComposedString(IElementType tokenType) {
        return tokenType == STRING_BEGIN;
    }

    public boolean parseWord(BashPsiBuilder builder) {
        return parseWord(builder, false);
    }

    public boolean parseWord(BashPsiBuilder builder, boolean enableRemapping) {
        return parseWord(builder, enableRemapping, TokenSet.EMPTY, TokenSet.EMPTY);
    }

    /**
     * Parses a word token. Several word tokens not seperated by whitespace are read
     * as a single word token.
     * <p/>
     * It accepts whitespace tokens in the beginning of the stream.
     * <p/>
     * A word can be a combination of several tokens, words are seperated by whitespace.
     *
     * @param builder         The builder
     * @param enableRemapping If the read tokens should be remapped.
     * @param reject          The tokens to reject. The tokens compared to this set are not yet remapped.
     * @param accept          Additional tokens which are accepted
     * @return True if a valid word could be read.
     */
    public boolean parseWord(BashPsiBuilder builder, boolean enableRemapping, TokenSet reject, TokenSet accept) {
        int processedTokens = 0;
        int parsedStringParts = 0;
        boolean firstStep = true;

        //if no token has been parsed yet we do accept whitespace in the beginning
        boolean isOk = true;

        PsiBuilder.Marker marker = builder.mark();

        while (isOk) {
            if (reject.contains(builder.getTokenType(!firstStep))) {
                break;
            }

            final IElementType nextToken = builder.getTokenType(!firstStep, enableRemapping);
            if (nextToken == WHITESPACE) {
                break;
            }

            if (nextToken == STRING_BEGIN) {
                isOk = parseComposedString(builder);
                parsedStringParts++;
            } else if (Parsing.braceExpansionParsing.isValid(builder)) {
                isOk = Parsing.braceExpansionParsing.parse(builder);
                processedTokens++;
            } else if (accept.contains(nextToken) || stringLiterals.contains(nextToken)) {
                builder.advanceLexer(true);
                processedTokens++;
            } else if (Parsing.var.isValid(builder)) {
                isOk = Parsing.var.parse(builder);
                processedTokens++;
            } else if (Parsing.shellCommand.backtickParser.isValid(builder)) {
                isOk = Parsing.shellCommand.backtickParser.parse(builder);
                processedTokens++;
            } else if (Parsing.shellCommand.conditionalExpressionParser.isValid(builder)) {
                isOk = Parsing.shellCommand.conditionalExpressionParser.parse(builder);
                processedTokens++;
            } else if (Parsing.processSubstitutionParsing.isValid(builder)) {
                isOk = Parsing.processSubstitutionParsing.parse(builder);
                processedTokens++;
            } else if (nextToken == LEFT_CURLY || nextToken == RIGHT_CURLY) {
                //fixme, is this proper parsing?
                //parsing token stream which is not a expansion but has curly brackets
                builder.advanceLexer();
                processedTokens++;
            } else if (nextToken == DOLLAR) {
                builder.advanceLexer();
                processedTokens++;
            } else { //either whitespace or unknown token
                break;
            }

            firstStep = false;
        }

        //either parsing failed or nothing has been found to parse
        if (!isOk || (processedTokens == 0 && parsedStringParts == 0)) {
            marker.drop();
            return false;
        }

        //a single string should not be parsed as a combined word element
        if (parsedStringParts >= 1 && processedTokens == 0) {
            marker.drop();
        } else {
            marker.done(PARSED_WORD_ELEMENT);
        }

        return true;
    }

    public boolean parseComposedString(BashPsiBuilder builder) {
        PsiBuilder.Marker stringStart = builder.mark();

        //eat STRING_START
        builder.advanceLexer(true);

        while (builder.getTokenType() != STRING_END) {
            boolean ok = false;

            if (Parsing.word.isWordToken(builder)) {
                ok = Parsing.word.parseWord(builder);
            } else if (Parsing.var.isValid(builder)) {
                ok = Parsing.var.parse(builder);
            }

            if (!ok) {
                stringStart.drop();
                return false;
            }
        }

        IElementType end = ParserUtil.getTokenAndAdvance(builder);
        if (end != STRING_END) {
            stringStart.error("String end marker not found");
            return false;
        }

        stringStart.done(STRING_ELEMENT);
        return true;
    }

    public boolean parseWordList(BashPsiBuilder builder, boolean readListTerminator, boolean enableRemapping) {
        if (!isWordToken(builder, enableRemapping)) {
            //ParserUtil.error(builder, "parser.unexpected.token");
            return false;
        }

        while (!builder.eof() && isWordToken(builder, enableRemapping)) {
            parseWord(builder, enableRemapping);
        }

        if (readListTerminator) {
            if (!Parsing.list.isListTerminator(builder.getTokenType())) {
                //ParserUtil.error(builder, "parser.unexpected.token");
                return false;
            }

            builder.advanceLexer(); //after the list terminator
        }

        return true;
    }
}
