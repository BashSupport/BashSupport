/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: MergingLexer.java, Class: MergingLexer
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

package com.ansorgit.plugins.bash.lang.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Lexer which can merge several tokens into a new token type.
 * This lexer takes a set of token merge definitions. Each of these definitions
 * are used to create a new, merged token stream at runtime.
 * <p/>
 * The base code was taken from MergingLexerAdapter of the OpenAPI.
 *
 * @author Joachim Ansorg
 */
class MergingLexer extends MergingLexerAdapterBase {

    /**
     * Create a merging lexer which works with the merge definitions given in the mergeTuples parameter.
     *
     * @param original    The original lexer, used as a delegate
     * @param mergeTuples The token merge definitions.
     */
    public MergingLexer(final Lexer original, final MergeTuple... mergeTuples) {
        super(original, new MergeFunction() {
            @Override
            public IElementType merge(IElementType type, Lexer lexer) {
                for (final MergeTuple currentTuple : mergeTuples) {
                    final TokenSet tokensToMerge = currentTuple.getTokensToMerge();

                    if (tokensToMerge.contains(type)) {
                        IElementType current = lexer.getTokenType();
                        //merge all upcoming tokens into the target token type
                        while (tokensToMerge.contains(current)) {
                            lexer.advance();

                            current = lexer.getTokenType();
                        }

                        return currentTuple.getTargetType();
                    }
                }

                return type;
            }
        });
    }
}
