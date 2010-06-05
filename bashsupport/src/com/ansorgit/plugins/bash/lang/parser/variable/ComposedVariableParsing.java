/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ComposedVariableParsing.java, Class: ComposedVariableParsing
 * Last modified: 2010-06-05
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

package com.ansorgit.plugins.bash.lang.parser.variable;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Parses a combined variable. A combined variable can be a subshell expression, a
 * parameter expansion or an arithmetic subexpression.
 * <p/>
 * Date: 02.05.2009
 * Time: 21:38:08
 *
 * @author Joachim Ansorg
 */
public class ComposedVariableParsing implements ParsingFunction {
    private static TokenSet acceptedStarts = TokenSet.create(LEFT_CURLY, LEFT_PAREN, EXPR_ARITH, EXPR_CONDITIONAL);

    public boolean isValid(BashPsiBuilder builder) {
        if (!ParserUtil.hasNextTokens(builder, DOLLAR)) {
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();
        try {
            ParserUtil.getTokenAndAdvance(builder); //DOLLAR
            return acceptedStarts.contains(builder.getTokenType(true));
        }
        finally {
            marker.rollbackTo();
        }
    }

    public boolean parse(BashPsiBuilder builder) {
        final PsiBuilder.Marker varMarker = builder.mark();
        builder.advanceLexer(true);

        final IElementType nextToken = builder.getTokenType(true);
        if (nextToken == WHITESPACE) {
            return false;
        }

        //check if a subshell of command group is following
        if (Parsing.parameterExpansionParsing.isValid(builder)) {
            Parsing.parameterExpansionParsing.parse(builder);
        } else if (Parsing.shellCommand.arithmeticParser.isValid(builder)) {
            Parsing.shellCommand.arithmeticParser.parse(builder);
        } else if (nextToken == LEFT_PAREN) {
            Parsing.shellCommand.subshellParser.parse(builder);
        } else {
            ParserUtil.error(varMarker, "parser.unexpected.token");
            return false;
        }

        varMarker.done(VAR_COMPOSED_VAR_ELEMENT);
        return true;
    }
}
