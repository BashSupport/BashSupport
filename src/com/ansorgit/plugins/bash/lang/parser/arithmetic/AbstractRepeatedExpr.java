/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractRepeatedExpr.java, Class: AbstractRepeatedExpr
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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Abstract class for a repeated expression which delegates the parsing of the subexpressions to
 * another parsing function.
 * <p/>
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 5:22:23 PM
 */
class AbstractRepeatedExpr implements ArithmeticParsingFunction {
    private final ArithmeticParsingFunction expressionParser;
    private final TokenSet operators;
    private final IElementType partMarker;
    private int maxRepeats;
    private final String debugInfo;
    private final boolean checkWhitespace;

    AbstractRepeatedExpr(ArithmeticParsingFunction expressionParser, TokenSet operators, IElementType partMarker, int maxRepeats, String debugInfo) {
        this.expressionParser = expressionParser;
        this.operators = operators;
        this.partMarker = partMarker;
        this.maxRepeats = maxRepeats;
        this.debugInfo = debugInfo;

        this.checkWhitespace = operators.contains(WHITESPACE);
    }

    public boolean isValid(BashPsiBuilder builder) {
        if (expressionParser.isValid(builder)) {
            return true;
        }

        PsiBuilder.Marker marker = builder.mark();

        ArithmeticParsingFunction parenthesisParser = ArithmeticFactory.parenthesisParser();
        boolean ok = parenthesisParser.isValid(builder)
                && parenthesisParser.parse(builder)
                && operators.contains(builder.getTokenType(checkWhitespace));

        marker.rollbackTo();

        return ok;
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        int count = 0;
        boolean ok;

        do {
            if (expressionParser.isValid(builder)) {
                ok = expressionParser.parse(builder);
            } else {
                ok = ArithmeticFactory.parenthesisParser().parse(builder);
            }

            count++;
        } while (ok && (maxRepeats <= 0 || count < maxRepeats) && ParserUtil.conditionalRead(builder, operators));

        if (count > 1 && partMarker != null) {
            marker.done(partMarker);
        } else {
            marker.drop();
        }

        return ok;
    }

    @Override
    public String toString() {
        return "RepeatedExpr:" + debugInfo + ": " + super.toString();
    }
}
