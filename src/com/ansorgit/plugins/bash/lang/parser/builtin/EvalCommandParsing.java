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

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * let Argument [Argument ...]
 * Each argument is evaluated as an arithmetic expression
 * <p/>
 * fixme this implementation is not yet complete, currently it is just eating the tokens to avoid syntax error markers
 * fixme not variable parsing, etc. is done at the moment
 */
class EvalCommandParsing implements ParsingFunction, ParsingTool {
    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == WORD && "eval".equals(builder.getTokenText());
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        //eat the "eval" token
        builder.advanceLexer();

        IElementType tokenType = builder.getTokenType();
        while (true) {
            PsiBuilder.Marker evalMarker = builder.mark();

            boolean ok = false;
            boolean emptyContainer = false;
            if (Parsing.word.isComposedString(tokenType)) {
                emptyContainer = builder.rawLookup(1) == BashTokenTypes.STRING_END;
                ok = Parsing.word.parseComposedString(builder);
            } else if (tokenType == WORD || tokenType == STRING2) {
                emptyContainer = builder.getTokenText() == null || builder.getTokenText().length() <= 2;
                builder.advanceLexer();
                ok = true;
            }

            if (!ok) {
                evalMarker.drop();
                break;
            }

            //do not mark empty strings as eval block (with PSI lazy parsing)
            if (emptyContainer) {
                evalMarker.drop();
            } else {
                evalMarker.collapse(EVAL_BLOCK);
            }

            tokenType = builder.getTokenType();
        }

        return true;
    }
}
