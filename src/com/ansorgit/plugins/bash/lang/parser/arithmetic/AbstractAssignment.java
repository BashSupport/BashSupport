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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.TokenSet;

/**
 * @author jansorg
 */
class AbstractAssignment implements ArithmeticParsingFunction {
    private static final TokenSet acceptedWords = TokenSet.create(WORD, ASSIGNMENT_WORD);

    private final ArithmeticParsingFunction next;
    private final TokenSet acceptedEqualTokens;

    public AbstractAssignment(ArithmeticParsingFunction next, TokenSet acceptedEqualTokens) {
        this.next = next;
        this.acceptedEqualTokens = acceptedEqualTokens;
    }

    public boolean isValid(BashPsiBuilder builder) {
        return acceptedWords.contains(builder.getTokenType()) || next.isValid(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        boolean ok = ParserUtil.conditionalRead(builder, acceptedWords);

        if (ok && acceptedEqualTokens.contains(builder.getTokenType())) {
            marker.done(VAR_DEF_ELEMENT);
            builder.advanceLexer();
        } else {
            marker.rollbackTo();
        }

        return next.parse(builder);
    }
}
