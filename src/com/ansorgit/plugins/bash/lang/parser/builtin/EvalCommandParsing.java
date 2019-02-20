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

import com.ansorgit.plugins.bash.lang.parser.*;
import com.ansorgit.plugins.bash.lang.parser.misc.RedirectionParsing;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;

/**
 * eval: eval [arg ...]
 * <br>
 * fixme this implementation is not yet complete, currently it is just eating the tokens to avoid syntax error markers
 * fixme not variable parsing, etc. is done at the moment
 */
class EvalCommandParsing implements ParsingFunction, ParsingTool {
    //this is a simple definition of allowed tokens per eval-code-block
    private static final TokenSet accepted = TokenSet.create(STRING2, ASSIGNMENT_WORD, EQ, WORD, VARIABLE, DOLLAR,
            LEFT_CURLY, RIGHT_CURLY);

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == WORD && "eval".equals(builder.getTokenText());
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        //eat the "eval" token
        builder.advanceLexer();

        while (true) {
            int start = builder.rawTokenIndex();

            //advance to the next non-whitespace token before reading an eval block
            builder.getTokenType();

            while (true) {
                //do nothing, the condition advances the PSI builder
                if (!(readEvaluatedBeforeCode(builder))) {
                    break;
                }
            }

            //advance to the next non-whitespace token before reading an eval block
            builder.getTokenType();

            RedirectionParsing.RedirectParseResult result = Parsing.redirection.parseSingleRedirectIfValid(builder, false);
            if (result != RedirectionParsing.RedirectParseResult.NO_REDIRECT) {
                //redirects must not be marked as code to be evaluated, so we avoid the marker block below
                if (result != RedirectionParsing.RedirectParseResult.OK) {
                    break;
                }

                continue;
            }

            PsiBuilder.Marker evalMarker = builder.mark();

            boolean ok;
            if (Parsing.word.isComposedString(builder.getTokenType(true))) {
                ok = Parsing.word.parseComposedString(builder);
            } else if (accepted.contains(builder.getTokenType(true))) {
                while (accepted.contains(builder.getTokenType(true))) {
                    builder.advanceLexer();
                }

                ok = true;
            } else {
                ok = false;
            }

            if (ok && builder.rawTokenIndex() > start) {
                evalMarker.collapse(EVAL_BLOCK);
            } else {
                evalMarker.drop();
                break;
            }
        }

        return true;
    }

    private boolean readEvaluatedBeforeCode(BashPsiBuilder builder) {
        OptionalParseResult result = Parsing.shellCommand.subshellParser.parseIfValid(builder);
        if (result.isValid()) {
            return result.isParsedSuccessfully();
        }

        result = Parsing.shellCommand.backtickParser.parseIfValid(builder);
        if (result.isValid()) {
            return result.isParsedSuccessfully();
        }

        result = ShellCommandParsing.arithmeticParser.parseIfValid(builder);
        if (result.isValid()) {
            return result.isParsedSuccessfully();
        }

        result = Parsing.shellCommand.conditionalCommandParser.parseIfValid(builder);
        if (result.isValid()) {
            return result.isParsedSuccessfully();
        }

        return false;
    }
}
