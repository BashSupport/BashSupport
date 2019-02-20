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

package com.ansorgit.plugins.bash.lang.parser.builtin.varDef;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing of variable definitions.
 * <br>
 *
 * @author jansorg
 */
abstract class AbstractVariableDefParsing implements BashTokenTypes {
    private static final TokenSet EQ_SET = TokenSet.create(BashTokenTypes.EQ);

    private final boolean acceptFrontVarDef;
    private final IElementType commandElementType;
    private final String commandName;
    private final boolean acceptArrayVars;
    private final CommandParsingUtil.Mode parsingMode;

    /**
     * Construct a new variable def commmand.
     *
     * @param acceptFrontVarDef    If true then local variable definitions are accepted in front of the command. e.g. "a=1 export b=1" is a valid bash command, but only b is visible afterwards.
     * @param commandElementType
     * @param commandText
     * @param acceptVarAssignments
     * @param acceptArrayVars
     */
    protected AbstractVariableDefParsing(boolean acceptFrontVarDef, IElementType commandElementType, String commandText, boolean acceptVarAssignments, boolean acceptArrayVars) {
        this.acceptFrontVarDef = acceptFrontVarDef;
        this.commandElementType = commandElementType;
        this.commandName = commandText;
        this.acceptArrayVars = acceptArrayVars;

        if (acceptVarAssignments) {
            parsingMode = CommandParsingUtil.Mode.LaxAssignmentMode;
        } else {
            parsingMode = CommandParsingUtil.Mode.SimpleMode;
        }
    }

    String getCommandName() {
        return commandName;
    }

    OptionalParseResult parseIfValid(BashPsiBuilder builder) {
        OptionalParseResult result = CommandParsingUtil.readAssignmentsAndRedirectsIfValid(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode, acceptArrayVars);
        if (acceptFrontVarDef && result.isValid() && !result.isParsedSuccessfully()) {
            throw new IllegalStateException("Unexpected state");
        }

        ParserUtil.markTokenAndAdvance(builder, commandElementType);

        // now read until we reach the first assignment
        if (!readOptions(builder)) {
            return OptionalParseResult.ParseError;
        }

        result = CommandParsingUtil.readAssignmentsAndRedirectsIfValid(builder, true, parsingMode, acceptArrayVars);
        if (!result.isValid() || result.isParsedSuccessfully()) {
            return OptionalParseResult.Ok;
        }
        return OptionalParseResult.ParseError;
    }

    private boolean readOptions(BashPsiBuilder builder) {
        // fixme optimize this for less isWordToken use
        while (Parsing.word.isWordToken(builder) && !isAssignment(builder)) {
            String argName = builder.getTokenText();

            boolean ok = Parsing.word.parseWordIfValid(builder, false, EQ_SET, TokenSet.EMPTY, null).isParsedSuccessfully();
            if (!ok) {
                return false;
            }

            if (argumentValueExpected(argName)) {
                ok = parseArgumentValue(argName, builder);
                if (!ok) {
                    return false;
                }
            }
        }

        return true;
    }

    protected boolean parseArgumentValue(String argName, BashPsiBuilder builder) {
        return Parsing.word.parseWordIfValid(builder, false, EQ_SET, TokenSet.EMPTY, null).isParsedSuccessfully();
    }

    boolean argumentValueExpected(String name) {
        return false;
    }

    boolean isAssignment(BashPsiBuilder builder) {
        String text = builder.getTokenText();
        if (text != null && !text.isEmpty() && text.charAt(0) == '-') {
            return false;
        }

        final PsiBuilder.Marker start = builder.mark();

        if (builder.getTokenType() == BashTokenTypes.ASSIGNMENT_WORD) {
            start.drop();
            return true;
        }

        OptionalParseResult result = Parsing.word.parseWordIfValid(builder, false, EQ_SET, TokenSet.EMPTY, null);
        if (result.isValid() && !result.isParsedSuccessfully()) {
            start.rollbackTo();
            return false;
        }

        //EQ or whitespace expected
        //IElementType next = builder.getTokenType(true);
        //return (next == BashTokenTypes.EQ || next == BashTokenTypes.WHITESPACE);
        //we either have a single word (no assignment) or a value assignment part

        start.rollbackTo();
        return true;
    }
}
