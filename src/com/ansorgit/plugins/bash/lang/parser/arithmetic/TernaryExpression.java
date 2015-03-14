/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: TernaryExpression.java, Class: TernaryExpression
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

/**
 * Parsing of ternary arithmetic expressions.
 * <p/>
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 4:29:05 PM
 */
class TernaryExpression implements ArithmeticParsingFunction {
    private ArithmeticParsingFunction next;

    TernaryExpression(ArithmeticParsingFunction next) {
        this.next = next;
    }

    public boolean isValid(BashPsiBuilder builder) {
        return next.isValid(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        boolean ok = next.parse(builder);

        if (ok && ParserUtil.conditionalRead(builder, ARITH_QMARK)) {
            ok = next.parse(builder);//check this
            ok = ok && ParserUtil.conditionalRead(builder, ARITH_COLON) && next.parse(builder);

            if (ok) {
                marker.done(ARITH_TERNERAY_ELEMENT);
            } else {
                marker.drop();
            }
        } else {
            marker.drop();
        }

        return ok;
    }
}