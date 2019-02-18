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

package com.ansorgit.plugins.bash.lang.parser.variable;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.misc.ShellCommandParsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;

/**
 * Parses a combined variable. A combined variable can be a subshell expression, a
 * parameter expansion or an arithmetic subexpression.
 * <br>
 *
 * @author jansorg
 */
public class ComposedVariableParsing implements ParsingFunction {
    private static final TokenSet acceptedStarts = TokenSet.create(
            LEFT_CURLY, LEFT_PAREN, EXPR_ARITH, EXPR_CONDITIONAL, EXPR_ARITH_SQUARE
    );

    public boolean isValid(BashPsiBuilder builder) {
        if (builder.rawLookup(0) != DOLLAR) {
            return false;
        }

        return acceptedStarts.contains(builder.rawLookup(1));
    }

    public boolean parse(BashPsiBuilder builder) {
        final PsiBuilder.Marker varMarker = builder.mark();
        builder.advanceLexer(); //DOLLAR token

        if (builder.getTokenType(true) == WHITESPACE) {
            varMarker.drop();
            return false;
        }

        //check if a subshell of command group is following
        boolean ok;
        OptionalParseResult result = Parsing.parameterExpansionParsing.parseIfValid(builder);
        if (result.isValid()) {
            ok = result.isParsedSuccessfully();
        } else {
            result = ShellCommandParsing.arithmeticParser.parseIfValid(builder);
            if (result.isValid()) {
                ok = result.isParsedSuccessfully();
            } else {
                result = Parsing.shellCommand.subshellParser.parseIfValid(builder);
                if (result.isValid()) {
                    ok = result.isParsedSuccessfully();
                } else {
                    ParserUtil.error(varMarker, "parser.unexpected.token");
                    return false;
                }
            }
        }

        if (ok) {
            varMarker.done(VAR_COMPOSED_VAR_ELEMENT);
        } else {
            varMarker.drop();
        }

        return true;
    }
}
