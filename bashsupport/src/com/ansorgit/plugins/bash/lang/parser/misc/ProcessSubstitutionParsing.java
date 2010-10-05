/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ProcessSubstitutionParsing.java, Class: ProcessSubstitutionParsing
 * Last modified: 2010-07-10
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parses process substitution expressions, i.e.
 * <p/>
 * <(bash subshell expression)
 * <p/>
 * >(bash susbshell expression)
 * <p/>
 * User: jansorg
 * Date: 10.07.2010
 * Time: 15:2
 * 3:12
 */
public class ProcessSubstitutionParsing implements ParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        if (builder.getTokenType() == LESS_THAN || builder.getTokenType() == GREATER_THAN) {
            PsiBuilder.Marker marker = builder.mark();

            try {
                builder.advanceLexer(true);

                return builder.getTokenType(true) == LEFT_PAREN;
            } finally {
                marker.rollbackTo();
            }
        }

        return false;
    }

    public boolean parse(BashPsiBuilder builder) {
        if (!isValid(builder)) {
            return false;
        }

        PsiBuilder.Marker marker = builder.mark();

        builder.getTokenType();
        builder.advanceLexer(); // first token

        IElementType second = builder.getTokenType(true);
        builder.advanceLexer(true);

        if (second != LEFT_PAREN) {
            marker.drop();
            return false;
        }

        boolean ok = Parsing.list.parseCompoundList(builder, true, false);

        if (!ok) {
            marker.drop();
            //fixme error message ?
            return false;
        }

        //eat the closing parenthesis
        if (!ParserUtil.conditionalRead(builder, RIGHT_PAREN)) {
            marker.drop();
            return false;
        }

        marker.done(PROCESS_SUBSTITUTION_ELEMENT);
        return true;
    }
}
