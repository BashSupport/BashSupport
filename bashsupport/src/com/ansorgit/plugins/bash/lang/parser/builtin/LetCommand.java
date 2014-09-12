/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: LetCommand.java, Class: LetCommand
 * Last modified: 2013-04-30
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * let Argument [Argument ...]
 * Each argument is evaluated as an arithmetic expression
 * <p/>
 * fixme this implementation is not yet complete, currently it is just eating the tokens to avoid syntax error markers
 * fixme not variable parsing, etc. is done at the moment
 */
class LetCommand implements ParsingFunction, ParsingTool {
    public static final TokenSet VALID_EXTRA_TOKENS = TokenSet.create(EQ, ADD_EQ, NUMBER, ARITH_PLUS, ARITH_ASS_PLUS);

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();
        String tokenText = builder.getTokenText();
        return tokenType == WORD && LanguageBuiltins.arithmeticCommands.contains(tokenText);
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        //eat the "let" token
        builder.advanceLexer();

        PsiBuilder.Marker letExpressionMarker = builder.mark();

        //read the params and redirects
        boolean paramsAreFine = CommandParsingUtil.readCommandParams(builder, VALID_EXTRA_TOKENS);

        if (paramsAreFine) {
            letExpressionMarker.collapse(BashElementTypes.LET_EXPRESSION);
            marker.done(GENERIC_COMMAND_ELEMENT);
        } else {
            letExpressionMarker.drop();
            marker.drop();
            builder.error("Expected an arithmetic expression");
        }

        return true;
    }
}
