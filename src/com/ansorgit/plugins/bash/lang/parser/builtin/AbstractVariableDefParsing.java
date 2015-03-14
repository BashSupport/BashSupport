/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractVariableDefParsing.java, Class: AbstractVariableDefParsing
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing of variable definitions.
 * <p/>
 * Date: 01.05.2009
 * Time: 20:59:13
 *
 * @author Joachim Ansorg
 */
abstract class AbstractVariableDefParsing implements ParsingFunction {
    private final boolean acceptFrontVarDef;
    private final IElementType commandElementType;
    private final String commandName;
    private final CommandParsingUtil.Mode parsingMode;

    /**
     * Construct a new variable def commmand.
     *
     * @param acceptFrontVarDef    If true then local variable definitions are accepted in front of the command. e.g. "a=1 export b=1" is a valid bash command, but only b is visible afterwards.
     * @param commandElementType
     * @param commandText
     * @param acceptVarAssignments
     */
    protected AbstractVariableDefParsing(boolean acceptFrontVarDef, IElementType commandElementType, String commandText, boolean acceptVarAssignments) {
        this.acceptFrontVarDef = acceptFrontVarDef;
        this.commandElementType = commandElementType;
        this.commandName = commandText;

        if (acceptVarAssignments) {
            parsingMode = CommandParsingUtil.Mode.LaxAssignmentMode;
        } else {
            parsingMode = CommandParsingUtil.Mode.SimpleMode;
        }
    }

    public final boolean isValid(BashPsiBuilder builder) {
        PsiBuilder.Marker start = builder.mark();

        //if accepted, read in command local var defs
        boolean ok = true;
        if (acceptFrontVarDef && CommandParsingUtil.isAssignmentOrRedirect(builder, CommandParsingUtil.Mode.StrictAssignmentMode)) {
            ok = CommandParsingUtil.readAssignmentsAndRedirects(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode);
        }

        //return ok && (builder.getTokenType() == INTERNAL_COMMAND) && commandName.equals(builder.getTokenText());
        String currentTokenText = builder.getTokenText();

        start.rollbackTo();

        return ok && LanguageBuiltins.isInternalCommand(currentTokenText) && commandName.equals(currentTokenText);
    }

    public boolean parse(BashPsiBuilder builder) {
        if (!isValid(builder)) {
            return false;
        }

        final PsiBuilder.Marker cmdMarker = builder.mark();

        if (acceptFrontVarDef && CommandParsingUtil.isAssignmentOrRedirect(builder, CommandParsingUtil.Mode.StrictAssignmentMode)) {
            boolean ok = CommandParsingUtil.readAssignmentsAndRedirects(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode);
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

        boolean ok = !CommandParsingUtil.isAssignmentOrRedirect(builder, parsingMode)
                || CommandParsingUtil.readAssignmentsAndRedirects(builder, true, parsingMode);

        if (ok) {
            cmdMarker.done(SIMPLE_COMMAND_ELEMENT);
            return true;
        } else {
            cmdMarker.drop();
            return false;
        }
    }

    private boolean readOptions(BashPsiBuilder builder) {
        builder.getTokenText();

        while (Parsing.word.isWordToken(builder) && !isAssignment(builder)) {
            boolean ok = Parsing.word.parseWord(builder);
            builder.getTokenText();

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
            builder.advanceLexer();
        } else if (Parsing.word.isWordToken(builder)) {
            if (!Parsing.word.parseWord(builder)) {
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
