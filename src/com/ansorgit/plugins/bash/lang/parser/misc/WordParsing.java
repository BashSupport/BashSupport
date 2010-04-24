/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: WordParsing.java, Class: WordParsing
 * Last modified: 2010-04-24
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

/**
 * Parsing of tokens which can be understood as word tokens, e.g. WORD, variables, subshell commands, etc.
 * <p/>
 * Date: 26.03.2009
 * Time: 15:55:24
 *
 * @author Joachim Ansorg
 */
public class WordParsing implements ParsingTool {
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
        return isComposedString(tokenType)
                || Parsing.braceExpansionParsing.isValid(builder)
                || BashTokenTypes.stringLiterals.contains(tokenType)
                || Parsing.var.isValid(builder)
                || Parsing.shellCommand.backquoteParser.isValid(builder)
                || Parsing.shellCommand.conditionalParser.isValid(builder)
                || tokenType == LEFT_CURLY;
    }

    public static boolean isComposedString(IElementType tokenType) {
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
     *
     * @param builder         The builder
     * @param enableRemapping If the read tokens should be remapped.
     * @param reject          The tokens to reject. The tokens compared to this set are not yet remapped.
     * @param accept
     * @return True if a valid word could be read.
     */
    public boolean parseWord(BashPsiBuilder builder, boolean enableRemapping, TokenSet reject, TokenSet accept) {
        //a word can be a comnination of several tokens, words are seperated by whitespace

        if (builder.getTokenType() == STRING_BEGIN) {
            return parseComposedString(builder);
        }

        PsiBuilder.Marker marker = builder.mark();
        boolean isOk = true;

        int processedTokens = 0;
        while (isOk) {
            if (reject.contains(builder.getTokenType(true))) {
                break;
            }

            final IElementType nextToken = builder.getTokenType(true, enableRemapping);

            if (nextToken == WHITESPACE) {
                break;
            }

            if (Parsing.braceExpansionParsing.isValid(builder)) {
                isOk = Parsing.braceExpansionParsing.parse(builder);
                processedTokens++;
            } else if (accept.contains(nextToken) || stringLiterals.contains(nextToken)) {
                builder.advanceLexer();
                processedTokens++;
            } else if (Parsing.var.isValid(builder)) {
                isOk = Parsing.var.parse(builder);
                processedTokens++;
            } else if (Parsing.shellCommand.backquoteParser.isValid(builder)) {
                isOk = Parsing.shellCommand.backquoteParser.parse(builder);
                processedTokens++;
            } else if (Parsing.shellCommand.conditionalParser.isValid(builder)) {
                isOk = Parsing.shellCommand.conditionalParser.parse(builder);
                processedTokens++;
            } else if (nextToken == LEFT_CURLY || nextToken == RIGHT_CURLY) {
                //fixme, is this proper parsing?
                //parsing token stream which is not a expansion but has curly brackets
                builder.advanceLexer();
                processedTokens++;
            } else { //either whitespace or unknown token
                break;
            }
        }

        if (processedTokens > 1) {
            marker.done(PARSED_WORD_ELEMENT);
        } else {
            marker.drop();
        }

        return isOk && (processedTokens > 0);
    }

    public static boolean parseComposedString(BashPsiBuilder builder) {
        PsiBuilder.Marker stringStart = builder.mark();

        builder.advanceLexer();//after STRING_START
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
