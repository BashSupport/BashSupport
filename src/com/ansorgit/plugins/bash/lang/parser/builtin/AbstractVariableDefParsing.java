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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parsing of variable definitions.
 * <br>
 * @author jansorg
 */
abstract class AbstractVariableDefParsing implements ParsingFunction {
    private static final TokenSet EQ_SET = TokenSet.create(EQ);

    private final boolean acceptFrontVarDef;
    private final IElementType commandElementType;
    private final String commandName;
    private final boolean acceptArrayVars;
    private final CommandParsingUtil.Mode parsingMode;

    /**
     * Construct a new variable def commmand.
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

    public final boolean isValid(BashPsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();

        // if accepted, read in command local var defs
        if (acceptFrontVarDef && CommandParsingUtil.isAssignmentOrRedirect(builder, CommandParsingUtil.Mode.StrictAssignmentMode, acceptArrayVars)) {
            if (!CommandParsingUtil.readAssignmentsAndRedirects(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode, acceptArrayVars)) {
                start.rollbackTo();
                return false;
            }
        }

        String currentTokenText = builder.getTokenText();

        start.rollbackTo();

        return LanguageBuiltins.isInternalCommand(currentTokenText, builder.isBash4()) && commandName.equals(currentTokenText);
    }

    public boolean parse(BashPsiBuilder builder) {
        if (!isValid(builder)) {
            return false;
        }

        final PsiBuilder.Marker cmdMarker = builder.mark();

        if (acceptFrontVarDef && CommandParsingUtil.isAssignmentOrRedirect(builder, CommandParsingUtil.Mode.StrictAssignmentMode, acceptArrayVars)) {
            boolean ok = CommandParsingUtil.readAssignmentsAndRedirects(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode, acceptArrayVars);
            if (!ok) {
                cmdMarker.drop();
                return false;
            }
        }

        final PsiBuilder.Marker cmdWordMarker = builder.mark();
        builder.advanceLexer(); //after the command name
        cmdWordMarker.done(commandElementType); //fixme check this for validity

        //now read until we reach the first assignment
        if (!readOptions(builder)) {
            cmdMarker.drop();
            return false;
        }

        boolean ok = !CommandParsingUtil.isAssignmentOrRedirect(builder, parsingMode, acceptArrayVars)
                || CommandParsingUtil.readAssignmentsAndRedirects(builder, true, parsingMode, acceptArrayVars);

        if (ok) {
            cmdMarker.done(SIMPLE_COMMAND_ELEMENT);
            return true;
        } else {
            cmdMarker.drop();
            return false;
        }
    }

    protected boolean readOptions(BashPsiBuilder builder) {
        builder.getTokenText();

        while (Parsing.word.isWordToken(builder) && !isAssignment(builder)) {
            boolean ok = Parsing.word.parseWord(builder, false, EQ_SET, TokenSet.EMPTY);
            //builder.getTokenText();

            if (!ok) {
                return false;
            }
        }

        return true;
    }

    boolean isAssignment(BashPsiBuilder builder) {
        String text = builder.getTokenText();
        if (text != null && text.length() > 0 && text.charAt(0) == '-') {
            return false;
        }

        final PsiBuilder.Marker start = builder.mark();

        if (builder.getTokenType() == BashTokenTypes.ASSIGNMENT_WORD) {
            start.drop();
            return true;
        } else if (Parsing.word.isWordToken(builder)) {
            if (!Parsing.word.parseWord(builder, false, EQ_SET, TokenSet.EMPTY)) {
                start.rollbackTo();

                return false;
            }
        }

        //EQ or whitespace expected
        //IElementType next = builder.getTokenType(true);
        //return (next == BashTokenTypes.EQ || next == BashTokenTypes.WHITESPACE);
        //we either have a single word (no assignment) or a value assignment part

        start.rollbackTo();
        return true;
    }
}
