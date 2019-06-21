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
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Parsing of tokens which can be understood as word tokens, e.g. WORD, variables, subshell commands, etc.
 * <br>
 *
 * @author jansorg
 */
public class WordParsing implements ParsingTool {
    private static final TokenSet singleDollarFollowups = TokenSet.create(STRING_END, WHITESPACE, LINE_FEED, SEMI);

    //fixme update callers! optimize
    public boolean isWordToken(BashPsiBuilder builder) {
        if (builder.eof()) {
            return false;
        }

        PsiBuilder.Marker mark = builder.mark();
        boolean valid = parseWordIfValid(builder).isValid();
        mark.rollbackTo();
        return valid;
    }

    public boolean isComposedString(IElementType tokenType) {
        return tokenType == STRING_BEGIN;
    }

    /**
     * Looks ahead if the current token is the start of a static double-quoted string value.
     *
     * @param builder         the current bulder
     * @param allowWhitespace if whitespace content is allowed
     * @return true if this is a quoted string which only consists of quotes and static string content
     */
    public boolean isSimpleComposedString(BashPsiBuilder builder, boolean allowWhitespace) {
        if (builder.getTokenType() != STRING_BEGIN) {
            return false;
        }

        TokenSet accepted = TokenSet.create(STRING_CONTENT);
        if (allowWhitespace) {
            accepted = TokenSet.orSet(accepted, TokenSet.create(WHITESPACE));
        }

        int lookahead = 1;
        while (accepted.contains(builder.rawLookup(lookahead))) {
            lookahead++;
        }

        if (builder.rawLookup(lookahead) != STRING_END) {
            return false;
        }

        IElementType end = builder.rawLookup(lookahead + 1);
        return end == null || end == WHITESPACE || end == LINE_FEED;
    }

    public OptionalParseResult parseWordIfValid(BashPsiBuilder builder) {
        return parseWordIfValid(builder, false);
    }

    public OptionalParseResult parseWordIfValid(BashPsiBuilder builder, boolean enableRemapping) {
        return parseWordIfValid(builder, enableRemapping, TokenSet.EMPTY, TokenSet.EMPTY, null);
    }

    /**
     * Parses a word token. Several word tokens not separated by whitespace are read
     * as a single word token.
     * <br>
     * It accepts whitespace tokens in the beginning of the stream.
     * <br>
     * A word can be a combination of several tokens, words are separated by whitespace.
     *
     * @param builder         The builder
     * @param enableRemapping If the read tokens should be remapped.
     * @param reject          The tokens to reject. The tokens compared to this set are not yet remapped.
     * @param accept          Additional tokens which are accepted
     * @param rejectTexts
     * @return The parse result
     */
    public OptionalParseResult parseWordIfValid(BashPsiBuilder builder, boolean enableRemapping, TokenSet reject, TokenSet accept, @Nullable Set<String> rejectTexts) {
        if (builder.eof()) {
            return OptionalParseResult.Invalid;
        }

        int processedTokens = 0;
        int parsedStringParts = 0;
        boolean firstStep = true;

        //if no token has been parsed yet we do accept whitespace in the beginning
        boolean isOk = true;

        PsiBuilder.Marker marker = builder.mark();

        while (isOk) {
            final IElementType rawCurrentToken = builder.rawLookup(0);
            if (rawCurrentToken == null) {
                break;
            }

            if (!firstStep && (rawCurrentToken == WHITESPACE || reject.contains(rawCurrentToken))) {
                break;
            }

            if (rejectTexts != null && rejectTexts.contains(builder.getTokenText(true))) {
                break;
            }

            final IElementType nextToken = enableRemapping ? builder.getRemappingTokenType() : builder.getTokenType();

            OptionalParseResult result = Parsing.braceExpansionParsing.parseIfValid(builder);
            if (result.isValid()) {
                isOk = result.isParsedSuccessfully();
                processedTokens++;
            } else if (nextToken == STRING_BEGIN) {
                isOk = parseComposedString(builder);
                parsedStringParts++;
            } else if (accept.contains(nextToken) || stringLiterals.contains(nextToken)) {
                builder.advanceLexer();
                processedTokens++;
            } else {
                result = Parsing.var.parseIfValid(builder);
                if (result.isValid()) {
                    isOk = result.isParsedSuccessfully();
                    processedTokens++;
                } else if (Parsing.shellCommand.backtickParser.isValid(builder)) {
                    isOk = Parsing.shellCommand.backtickParser.parse(builder);
                    processedTokens++;
                } else if (Parsing.shellCommand.conditionalExpressionParser.isValid(builder)) {
                    isOk = Parsing.shellCommand.conditionalExpressionParser.parse(builder);
                    processedTokens++;
                } else if (Parsing.shellCommand.historyExpansionParser.isValid(builder)) {
                    isOk = Parsing.shellCommand.historyExpansionParser.parse(builder);
                    processedTokens++;
                } else if (Parsing.processSubstitutionParsing.isValid(builder)) {
                    isOk = Parsing.processSubstitutionParsing.parse(builder);
                    processedTokens++;
                } else if (nextToken == LEFT_CURLY || !firstStep && nextToken == RIGHT_CURLY) {
                    //fixme, is this proper parsing?
                    //parsing token stream which is not an expansion but has curly brackets
                    builder.advanceLexer();
                    processedTokens++;
                } else if (nextToken == DOLLAR) {
                    IElementType followup = builder.rawLookup(1);
                    if (followup == null || singleDollarFollowups.contains(followup)) {
                        builder.advanceLexer();
                        processedTokens++;
                    } else {
                        break;
                    }
                } else if (!firstStep && nextToken == EQ) {
                    builder.advanceLexer();
                    processedTokens++;
                } else if (ParserUtil.isWord(builder, "!")) {
                    IElementType followup = builder.rawLookup(1);
                    if ((followup == null || ParserUtil.isWhitespaceOrLineFeed(followup))) {
                        //either a single ! token with following whitespace or at the end of the file
                        builder.advanceLexer();
                        processedTokens++;
                    } else {
                        break;
                    }
                } else { //either whitespace or unknown token
                    break;
                }
            }

            firstStep = false;
        }

        //either parsing failed or nothing has been found to parse
        if (processedTokens == 0 && parsedStringParts == 0) {
            marker.drop();
            return OptionalParseResult.Invalid;
        }

        if (!isOk) {
            marker.drop();
            return OptionalParseResult.ParseError;
        }

        //a single string should not be parsed as a combined word element
        if (parsedStringParts >= 1 && processedTokens == 0) {
            marker.drop();
        } else {
            marker.done(PARSED_WORD_ELEMENT);
        }

        return OptionalParseResult.Ok;
    }

    public boolean parseComposedString(BashPsiBuilder builder) {
        PsiBuilder.Marker stringStart = builder.mark();

        //eat STRING_START
        builder.advanceLexer();

        while (builder.getTokenType() != STRING_END) {
            boolean ok = false;

            if (builder.getTokenType() == STRING_CONTENT) {
                builder.advanceLexer();
                ok = true;
            } else {
                OptionalParseResult varResult = Parsing.var.parseIfValid(builder);
                if (varResult.isValid()) {
                    ok = varResult.isParsedSuccessfully();
                } else if (Parsing.shellCommand.backtickParser.isValid(builder)) {
                    ok = Parsing.shellCommand.backtickParser.parse(builder);
                }
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

    public OptionalParseResult parseWordListIfValid(BashPsiBuilder builder, boolean readListTerminator, boolean enableRemapping) {
        if (builder.eof()) {
            return OptionalParseResult.Invalid;
        }

        while (!builder.eof()) {
            OptionalParseResult result = parseWordIfValid(builder, enableRemapping);
            if (result == OptionalParseResult.Invalid) {
                break;
            }
            if (!result.isParsedSuccessfully()) {
                return OptionalParseResult.ParseError;
            }
        }

        if (readListTerminator) {
            if (!Parsing.list.isListTerminator(builder.getTokenType())) {
                return OptionalParseResult.ParseError;
            }
            builder.advanceLexer(); //after the list terminator
        }

        return OptionalParseResult.Ok;
    }
}
