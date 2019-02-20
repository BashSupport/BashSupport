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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.misc.RedirectionParsing;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.ansorgit.plugins.bash.util.NullMarker;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing function for commands.
 * <br>
 *
 * @author jansorg
 */
public class CommandParsingUtil implements BashTokenTypes, BashElementTypes {
    private final static TokenSet assignmentSeparators = TokenSet.create(LINE_FEED, SEMI, WHITESPACE);
    private final static TokenSet validWordTokens = TokenSet.create(ARITH_NUMBER);

    private CommandParsingUtil() {
    }

    public static boolean readCommandParams(final BashPsiBuilder builder) {
        return readCommandParams(builder, TokenSet.EMPTY);
    }

    /**
     * Reads a list of optional command params.
     *
     * @param builder
     * @param validExtraTokens
     * @return True if the list is either empty or parsed fine.
     */
    public static boolean readCommandParams(final BashPsiBuilder builder, TokenSet validExtraTokens) {
        boolean ok = true;

        while (!builder.eof() && ok) {
            RedirectionParsing.RedirectParseResult result = Parsing.redirection.parseListIfValid(builder, true);
            if (result != RedirectionParsing.RedirectParseResult.NO_REDIRECT) {
                ok = result != RedirectionParsing.RedirectParseResult.PARSING_FAILED;
            } else {
                OptionalParseResult parseResult = Parsing.word.parseWordIfValid(builder, true);
                if (parseResult.isValid()) {
                    ok = parseResult.isParsedSuccessfully();
                } else if (validExtraTokens.contains(builder.getTokenType())) {
                    builder.advanceLexer();
                    ok = true;
                } else {
                    break;
                }
            }
        }

        return ok;
    }

    public static boolean readOptionalAssignmentOrRedirects(BashPsiBuilder builder, Mode asssignmentMode, boolean markAsVarDef, boolean acceptArrayVars) {
        boolean ok = true;
        while (ok) {
            OptionalParseResult result = readAssignmentsAndRedirectsIfValid(builder, markAsVarDef, asssignmentMode, acceptArrayVars);
            if (!result.isValid()) {
                break;
            }
            ok = result.isParsedSuccessfully();
        }
        return ok;
    }

    /**
     * Reads an optional list of assignments and redirects which are before a command.
     *
     * @param builder         The current builder
     * @param markAsVarDef    Mark as a variable definition
     * @param mode
     * @param acceptArrayVars
     * @return
     */
    public static OptionalParseResult readAssignmentsAndRedirectsIfValid(final BashPsiBuilder builder, boolean markAsVarDef, Mode mode, boolean acceptArrayVars) {
        boolean ok = false;
        int count = 0;

        do {
            OptionalParseResult parseResult = readAssignmentIfValid(builder, mode, markAsVarDef, acceptArrayVars);
            if (parseResult.isValid()) {
                ok = parseResult.isParsedSuccessfully();
            } else {
                RedirectionParsing.RedirectParseResult result = Parsing.redirection.parseSingleRedirectIfValid(builder, true);
                if (result != RedirectionParsing.RedirectParseResult.NO_REDIRECT) {
                    if (result == RedirectionParsing.RedirectParseResult.INVALID_REDIRECT) {
                        builder.error("Invalid redirect");
                    }
                    ok = result == RedirectionParsing.RedirectParseResult.OK || result == RedirectionParsing.RedirectParseResult.INVALID_REDIRECT;
                } else if (mode == Mode.LaxAssignmentMode) {
                    parseResult = Parsing.word.parseWordIfValid(builder);
                    if (parseResult.isValid()) {
                        ok = parseResult.isParsedSuccessfully();
                    } else {
                        break;
                    }
                } else {
                    break;
                }
            }
            count++;
        } while (ok && !builder.eof());

        if (!ok && count == 0) {
            return OptionalParseResult.Invalid;
        }
        return ok ? OptionalParseResult.Ok : OptionalParseResult.ParseError;
    }

    /**
     * Reads a single assignment
     *
     * @param builder         Provides the tokens
     * @param mode            Set to true if a variable assignment with "declare" is being processed right now.
     * @param markAsVarDef    True if the assignments should be marked with a psi marker as such.
     * @param acceptArrayVars
     * @return True if the assignment has been read successfully.
     */
    public static OptionalParseResult readAssignmentIfValid(BashPsiBuilder builder, Mode mode, boolean markAsVarDef, boolean acceptArrayVars) {
        boolean isValid;
        switch (mode) {
            case SimpleMode:
                isValid = (acceptArrayVars && ParserUtil.hasNextTokens(builder, false, ASSIGNMENT_WORD, LEFT_SQUARE))
                        || ParserUtil.isWordToken(builder.getTokenType())
                        || Parsing.word.isWordToken(builder);
                break;
            case LaxAssignmentMode:
                isValid = builder.getTokenType() == ASSIGNMENT_WORD
                        || ParserUtil.isWordToken(builder.getTokenType())
                        || Parsing.word.isWordToken(builder); //fixme optimize
                break;
            default:
                isValid = builder.getTokenType() == ASSIGNMENT_WORD || (builder.isEvalMode() && ParserUtil.hasNextTokens(builder, false, VARIABLE, EQ));
        }

        if (!isValid) {
            return OptionalParseResult.Invalid;
        }


        PsiBuilder.Marker assignment = builder.mark();

        switch (mode) {
            case SimpleMode:
                if (acceptArrayVars && ParserUtil.hasNextTokens(builder, false, ASSIGNMENT_WORD, LEFT_SQUARE)) {
                    break;
                }

                OptionalParseResult result = Parsing.word.parseWordIfValid(builder);
                if (!result.isParsedSuccessfully()) {
                    assignment.drop();
                    return result;
                }
                break;

            case LaxAssignmentMode:
                if (builder.getTokenType() == ASSIGNMENT_WORD) {
                    builder.advanceLexer();
                } else {
                    OptionalParseResult varResult = Parsing.var.parseIfValid(builder);
                    if (varResult.isValid()) {
                        assignment.drop();

                        if (!varResult.isParsedSuccessfully()) {
                            return varResult;
                        }

                        //dummy marker because we must not mark a dynamic variable name (as in 'export $a=42)'
                        assignment = NullMarker.get();
                    } else {
                        result = Parsing.word.parseWordIfValid(builder, false, BashTokenTypes.EQ_SET, TokenSet.EMPTY, null);
                        if (!result.isParsedSuccessfully()) {
                            assignment.drop();
                            return result;
                        }
                    }
                }
                break;

            case StrictAssignmentMode: {
                if (builder.isEvalMode() && ParserUtil.hasNextTokens(builder, false, VARIABLE, EQ)) {
                    //assignment with variable on the left
                    markAsVarDef = false;
                    result = Parsing.var.parseIfValid(builder);
                    if (!result.isParsedSuccessfully()) {
                        assignment.drop();
                        return result;
                    }

                    break;
                }

                final IElementType nextToken = ParserUtil.getTokenAndAdvance(builder);
                if (nextToken != ASSIGNMENT_WORD) {
                    ParserUtil.error(assignment, "parser.unexpected.token");
                    return OptionalParseResult.ParseError;
                }
                break;
            }

            default:
                assignment.drop();
                throw new IllegalStateException("Invalid parsing mode found");
        }

        if (mode == Mode.SimpleMode && acceptArrayVars && builder.getTokenType() == ASSIGNMENT_WORD) {
            //the accept array vars is only evaluated in simple mode, e.g. simple variable use
            //the other modes parse the array index with assignment following later on
            builder.advanceLexer();

            //if it has the [] marker
            boolean hasArrayIndex = readArrayIndex(builder, assignment);
            if (!hasArrayIndex) {
                //error parsing the array index marker, if it was present
                return OptionalParseResult.ParseError;
            }
        }

        if (mode != Mode.SimpleMode) {
            if (!readArrayIndex(builder, assignment)) {
                //error parsing the array index marker, if it was present
                return OptionalParseResult.ParseError;
            }

            //here the next token should be the EQ token, i.e. after the element reference part
            final IElementType nextToken = builder.getTokenType(true);
            boolean hasAssignment = nextToken == EQ || nextToken == ADD_EQ;
            if (!hasAssignment && mode == Mode.StrictAssignmentMode) {
                ParserUtil.error(assignment, "parser.unexpected.token");
                return OptionalParseResult.ParseError;
            }

            if (hasAssignment) {
                builder.advanceLexer();
            }

            // now parse the assignment if it's available
            // we've already checked if the assignment is mandatory
            if (hasAssignment) {
                if (builder.getTokenType(true) == LEFT_PAREN) {
                    //assignment list for an array
                    final boolean ok = parseAssignmentList(builder);
                    if (!ok) {
                        ParserUtil.error(builder, "parser.unexpected.token");
                        assignment.drop();
                        return OptionalParseResult.ParseError;
                    }
                }

                final IElementType token = builder.getTokenType(true);
                final boolean isEndToken = assignmentSeparators.contains(token);
                if (token != null && !isEndToken) {
                    if (!Parsing.word.parseWordIfValid(builder, true, TokenSet.EMPTY, validWordTokens, null).isParsedSuccessfully()) {
                        ParserUtil.error(builder, "parser.unexpected.token");
                        assignment.drop();
                        return OptionalParseResult.ParseError;
                    }
                }
            }
        }

        if (markAsVarDef) {
            assignment.done(VAR_DEF_ELEMENT);
        } else {
            assignment.drop();
        }

        return OptionalParseResult.Ok;
    }

    /**
     * Parses an assignment list like a=(1 2 3)
     * Grammar (selfmade):
     * assignment_list ::= "(" array_assignment {" " {array_assignment}* ")"
     * <br>
     * array_assignment ::=
     * WORD_EXPR
     * |    STRING
     * |    STRING2
     * |   "[ arith_expression "]"=(WORD|STRING|STRING2)
     *
     * @param builder
     * @return
     */
    public static boolean parseAssignmentList(BashPsiBuilder builder) {
        IElementType first = builder.getTokenType();
        if (first != LEFT_PAREN) {
            builder.advanceLexer(); //make sure that at lease the first token is read   
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();
        builder.advanceLexer(); //the left paren token

        while (!builder.eof() && (builder.getTokenType(true) != RIGHT_PAREN)) {
            //optional newlines at the beginning
            builder.readOptionalNewlines();

            if (builder.getTokenType() == LEFT_SQUARE) {
                //array value assignment to specific position
                boolean ok = ShellCommandParsing.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                if (!ok) {
                    marker.drop();
                    return false;
                }

                //now we expect an equal sign
                final IElementType eqToken = ParserUtil.getTokenAndAdvance(builder);
                if (eqToken != EQ) {
                    marker.drop();
                    return false;
                }

                //continued below
            }

            OptionalParseResult result = Parsing.word.parseWordIfValid(builder, true);
            if (result.isValid()) {
                if (!result.isParsedSuccessfully()) {
                    marker.drop();
                    return false;
                }
            }

            //optional newlines after the comma
            boolean hadNewlines = builder.readOptionalNewlines(-1, true);

            //whitespace tokens separate the array assignment values
            //if the next token is not whitespace, we break the loop, cause we're at the last element
            if (!hadNewlines && builder.getTokenType(true) != WHITESPACE) {
                break;
            }

            //the current RAW token is whitespace, but the non-raw token is already the next,
            // i.e. the closing bracket or the start of the next value
            //don't: builder.advanceLexer();
        }

        if (!(ParserUtil.getTokenAndAdvance(builder) == RIGHT_PAREN)) {
            marker.drop();
            return false;
        }

        marker.done(VAR_ASSIGNMENT_LIST);
        return true;
    }

    private static boolean readArrayIndex(BashPsiBuilder builder, PsiBuilder.Marker assignment) {
        if (builder.getTokenType() == LEFT_SQUARE) {
            //this is an array assignment, e.g. a[1]=x
            //parse the arithmetic expression in the array assignment square brackets
            boolean valid = ShellCommandParsing.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
            if (!valid) {
                ParserUtil.error(builder, "parser.unexpected.token");
                assignment.drop();
                return false;
            }
        }

        return true;
    }

    public enum Mode {
        /**
         * Only accept an ASSIGNENT_WORD or ARRAY_ASSIGNMENT_WORD in front .
         * The =value part is mandatory.
         */
        StrictAssignmentMode,
        /**
         * Optional =value part and allows simple word tokens and variable names in front
         */
        LaxAssignmentMode,
        /**
         * Only single word tokens are valid, used for read commands.
         */
        SimpleMode
    }

}
