/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: CommandParsingUtil.java, Class: CommandParsingUtil
 * Last modified: 2010-05-11
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

package com.ansorgit.plugins.bash.lang.parser.command;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing function for commands.
 * <p/>
 * User: jansorg
 * Date: Nov 26, 2009
 * Time: 9:16:36 PM
 */
public class CommandParsingUtil implements BashTokenTypes, BashElementTypes {
    private final static TokenSet assignmentSeparators = TokenSet.create(LINE_FEED, SEMI, WHITESPACE);
    private final static TokenSet validWordTokens = TokenSet.create(NUMBER);

    /**
     * Reads a list of optional command params.
     *
     * @param builder
     * @return True if the list is either empty or parsed fine.
     */
    public static boolean readCommandParams(final BashPsiBuilder builder) {
        boolean ok = true;

        while (!builder.eof() && ok) {
            if (Parsing.redirection.isRedirect(builder)) {
                ok = Parsing.redirection.parseList(builder, false);
            } else if (Parsing.word.isWordToken(builder, true)) {
                ok = Parsing.word.parseWord(builder, true);
            } else {
                break;
            }
        }

        return ok;
    }

    public static enum Mode {
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

    private CommandParsingUtil() {
    }

    public static boolean isAssignment(final BashPsiBuilder builder, Mode mode) {
        final IElementType tokenType = builder.getTokenType();
        switch (mode) {
            case SimpleMode:
                return ParserUtil.isWordToken(tokenType)
                        || Parsing.word.isWordToken(builder)
                        || Parsing.var.isValid(builder);

            case LaxAssignmentMode:
                return tokenType == ASSIGNMENT_WORD
                        || tokenType == ARRAY_ASSIGNMENT_WORD
                        || ParserUtil.isWordToken(tokenType)
                        || Parsing.word.isWordToken(builder)
                        || Parsing.var.isValid(builder);

            default:
                return tokenType == ASSIGNMENT_WORD
                        || tokenType == ARRAY_ASSIGNMENT_WORD; //fixme
        }
    }


    public static boolean isAssignmentOrRedirect(BashPsiBuilder builder, Mode assignmentMode) {
        return isAssignment(builder, assignmentMode) || Parsing.redirection.isRedirect(builder);
    }

    /**
     * Reads an optional list of assignments and redirects which
     * are before a command.
     *
     * @param builder
     * @param markAsVarDef
     * @param mode
     * @return
     */
    public static boolean readAssignmentsAndRedirects(final BashPsiBuilder builder, boolean markAsVarDef, Mode mode) {
        boolean ok = false;

        do {
            if (isAssignment(builder, mode)) {
                ok = readAssignment(builder, mode, markAsVarDef);
            } else if (Parsing.redirection.isRedirect(builder)) {
                ok = Parsing.redirection.parseSingleRedirect(builder);
            } else if (mode == Mode.LaxAssignmentMode && Parsing.word.isWordToken(builder)) {
                ok = Parsing.word.parseWord(builder);
            } else {
                break;
            }
        } while (!builder.eof() && ok);

        return ok;
    }


    /**
     * Reads a single assignment
     *
     * @param builder      Provides the okens
     * @param mode         Set to true if a variable assignment with "declare" is being processed right now.
     * @param markAsVarDef True if the assignments should be marked with a psi marker as such.
     * @return True if the assignment has been read successfully.
     */
    public static boolean readAssignment(BashPsiBuilder builder, Mode mode, boolean markAsVarDef) {
        final PsiBuilder.Marker assignment = builder.mark();

        switch (mode) {
            case SimpleMode:
                if (!Parsing.word.parseWord(builder)) {
                    assignment.drop();
                    return false;
                }
                break;

            case LaxAssignmentMode:
                if (builder.getTokenType() == ASSIGNMENT_WORD) {
                    builder.advanceLexer();
                } else if (Parsing.var.isValid(builder)) {
                    if (!Parsing.var.parse(builder)) {
                        assignment.drop();
                        return false;
                    }
                } else if (!Parsing.word.parseWord(builder)) {
                    assignment.drop();
                    return false;
                }
                break;

            case StrictAssignmentMode: {
                final IElementType assignmentWord = ParserUtil.getTokenAndAdvance(builder);
                if (assignmentWord != ASSIGNMENT_WORD && assignmentWord != ARRAY_ASSIGNMENT_WORD) {
                    ParserUtil.error(assignment, "parser.unexpected.token");
                    return false;
                }
                break;
            }
            default:
                throw new IllegalStateException("Invalid parsing mode found");
        }

        if (mode != Mode.SimpleMode) {
            final IElementType eqToken = builder.getTokenType(true);
            boolean hasAssignment = eqToken == EQ || eqToken == ADD_EQ;
            if (!hasAssignment && mode == Mode.StrictAssignmentMode) {
                ParserUtil.error(assignment, "parser.unexpected.token");
                return false;
            }

            if (hasAssignment) {
                builder.advanceLexer(true);
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
                        return false;
                    }
                }

                final IElementType token = builder.getTokenType(true);
                final boolean isEndToken = assignmentSeparators.contains(token);
                if (token != null && !isEndToken) {
                    if (!Parsing.word.parseWord(builder, true, TokenSet.EMPTY, validWordTokens)) {
                        ParserUtil.error(builder, "parser.unexpected.token");
                        assignment.drop();
                        return false;
                    }
                }
            }
        }

        if (markAsVarDef) {
            assignment.done(VAR_DEF_ELEMENT);
        } else {
            assignment.drop();
        }

        return true;
    }

    /**
     * Parses an assignment list like a=(1 2 3)
     * Grammar (selfmade):
     * assignment_list ::= "(" array_assignment {" " {array_assignment}* ")"
     * <p/>
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
        final IElementType first = ParserUtil.getTokenAndAdvance(builder);
        if (first != LEFT_PAREN) {
            return false;
        }

        while (!builder.eof() && (builder.getTokenType() != RIGHT_PAREN)) {
            //optional newlines at the beginning
            builder.eatOptionalNewlines();

            if (builder.getTokenType() == LEFT_SQUARE) {
                //array value assignment to specific position
                boolean ok = Parsing.shellCommand.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                if (!ok) {
                    return false;
                }

                //now we expect an equal sign
                final IElementType eqToken = ParserUtil.getTokenAndAdvance(builder);
                if (eqToken != EQ) {
                    return false;
                }

                //continued below
            }

            if (Parsing.word.isWordToken(builder)) {
                final boolean ok = Parsing.word.parseWord(builder, true);

                if (!ok) {
                    return false;
                }
            }

            //optional newlines after the comma
            builder.eatOptionalNewlines(-1, true);

            //if the next token is not a comma, we break the loop, cause we're at the last element
            if (builder.getTokenType(true) == WHITESPACE) {
                builder.advanceLexer(true);
            } else {
                break;
            }
        }

        return ParserUtil.getTokenAndAdvance(builder) == RIGHT_PAREN;
    }

}
