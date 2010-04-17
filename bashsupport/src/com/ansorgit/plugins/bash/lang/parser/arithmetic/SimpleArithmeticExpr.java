/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: SimpleArithmeticExpr.java, Class: SimpleArithmeticExpr
 * Last modified: 2010-04-17
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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing of a simple arithmetic expressions.
 * <p/>
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 5:52:20 PM
 */
class SimpleArithmeticExpr implements ArithmeticParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();
        return tokenType == WORD || tokenType == NUMBER || Parsing.var.isValid(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        boolean ok = false;

        if (arithmeticAdditionOps.contains(builder.getTokenType())) {
            builder.advanceLexer(); //eat the prefix - or + token
            ok = this.parse(builder);
        } else if (Parsing.var.isValid(builder)) {
            ok = Parsing.var.parse(builder);
        } else {
            IElementType tokenType = builder.getTokenType();
            if (tokenType == WORD) {
                ParserUtil.markTokenAndAdvance(builder, VAR_ELEMENT);
                ok = true;
            } else if (tokenType == NUMBER) {
                builder.advanceLexer();
                ok = true;
            }
        }

        if (ok) {
            marker.done(ARITH_SIMPLE_ELEMENT);
        } else {
            marker.drop();
        }

        return ok;
    }

    public boolean partialParsing(BashPsiBuilder builder) {
        return false;
    }

    public boolean isValidPartial(BashPsiBuilder builder) {
        return false;
    }
}
