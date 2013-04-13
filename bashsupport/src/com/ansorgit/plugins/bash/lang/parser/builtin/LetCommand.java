/*
 * Copyright 2013 Joachim Ansorg, mail@ansorg-it.com
 * File: LetCommand.java, Class: LetCommand
 * Last modified: 2013-04-06
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
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticFactory;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticParsingFunction;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * let Argument [Argument ...]
 * Each argument is evaluated as an arithmetic expression
 */
class LetCommand implements ParsingFunction, ParsingTool {
    private TokenSet END_TOKENS = TokenSet.create(SEMI, LINE_FEED);

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

        // if (builder.getTokenType(true) == WHITESPACE) {
        // }

        ArithmeticParsingFunction arithParser = ArithmeticFactory.entryPoint();
        boolean ok = false;

        while (builder.getTokenType(true) == WHITESPACE) {
            //builder.advanceLexer(true);

            ok = arithParser.parse(builder);
        }

        if (ok) {
            marker.done(GENERIC_COMMAND_ELEMENT);
        } else {
            marker.drop();
            builder.error("Expected an arithmetic expression");
        }

        return ok;
    }
}
