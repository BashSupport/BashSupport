/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: AbstractAssignment.java, Class: AbstractAssignment
 * Last modified: 2010-02-06
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
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 6:31:03 PM
 */
public class AbstractAssignment implements ParsingFunction {
    private final ParsingFunction next;
    private final TokenSet acceptedEqualTokens;
    private TokenSet acceptedWords = TokenSet.create(WORD, ASSIGNMENT_WORD);

    public AbstractAssignment(ParsingFunction next, TokenSet acceptedEqualTokens) {
        this.next = next;
        this.acceptedEqualTokens = acceptedEqualTokens;
    }

    public boolean isValid(IElementType token) {
        throw new IllegalStateException("unsupported");
    }

    public boolean isValid(BashPsiBuilder builder) {
        return acceptedWords.contains(builder.getTokenType()) || next.isValid(builder);
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        boolean ok = ParserUtil.conditionalRead(builder, acceptedWords) && acceptedEqualTokens.contains(builder.getTokenType());
        if (ok) {
            marker.done(VAR_DEF_ELEMENT);
            builder.advanceLexer();    //eat the assignment operator
        } else {
            marker.rollbackTo();
        }

        return next.parse(builder);
    }
}
