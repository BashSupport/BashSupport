/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: MergingLexer.java, Class: MergingLexer
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerBase;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Lexer which can merge several tokens into a new token type.
 * This lexer takes a set of token merge definitions. Each of these definitions
 * are used to create a new, merged token stream at runtime.
 * <p/>
 * Date: 17.04.2009
 * Time: 11:38:27
 * <p/>
 * The base code was taken from MergingLexerAdapter of the OpenAPI.
 *
 * @author Joachim Ansorg
 */
class MergingLexer extends LexerBase {
    private final Lexer originalLexer;
    private final MergeTuple[] mergeTuples;
    private IElementType myResultToken;
    private int myState;
    private int myTokenStart;

    /**
     * Create a merging lexer which works with the merge definitions given in the mergeTuples paramter.
     *
     * @param original    The original lexer, used as a delegate
     * @param mergeTuples The token merge definitions.
     */
    public MergingLexer(Lexer original, MergeTuple... mergeTuples) {
        originalLexer = original;
        this.mergeTuples = mergeTuples;
    }

    @Override
    public void start(final CharSequence buffer, final int startOffset, final int endOffset, final int initialState) {
        originalLexer.start(buffer, startOffset, endOffset, initialState);
        myResultToken = null;
    }

    public CharSequence getBufferSequence() {
        return originalLexer.getBufferSequence();
    }

    public int getState() {
        locateToken();
        return myState;
    }

    public IElementType getTokenType() {
        locateToken();
        return myResultToken;
    }

    public int getTokenStart() {
        locateToken();
        return myTokenStart;
    }

    public int getTokenEnd() {
        locateToken();
        return originalLexer.getTokenStart();
    }

    public void advance() {
        myResultToken = null;
    }

    public int getBufferEnd() {
        return originalLexer.getBufferEnd();
    }

    private void locateToken() {
        if (myResultToken == null) {
            IElementType currentToken = originalLexer.getTokenType();
            myTokenStart = originalLexer.getTokenStart();
            myState = originalLexer.getState();

            if (currentToken == null) return;
            originalLexer.advance();

            boolean found = false;
            for (int i = 0; i < mergeTuples.length && !found; i++) {
                final MergeTuple currentTuple = mergeTuples[i];
                final TokenSet myTokensToMerge = currentTuple.getTokensToMerge();

                found = myTokensToMerge.contains(currentToken);
                if (found) {
                    myResultToken = currentTuple.getTargetType();

                    //merge all upcoming tokens
                    while (myTokensToMerge.contains(currentToken)) {
                        currentToken = originalLexer.getTokenType();

                        if (myTokensToMerge.contains(currentToken)) originalLexer.advance();
                    }
                }
            }

            if (!found) {
                myResultToken = currentToken;
            }
        }
    }

    public void restore(LexerPosition position) {
        MyLexerPosition pos = (MyLexerPosition) position;

        originalLexer.restore(pos.getOriginalPosition());
        myResultToken = pos.getType();
        myTokenStart = pos.getOffset();
        myState = pos.getOldState();
    }

    public LexerPosition getCurrentPosition() {
        return new MyLexerPosition(myTokenStart, myResultToken, originalLexer.getCurrentPosition(), myState);
    }

    private static class MyLexerPosition implements LexerPosition {
        private final int myOffset;
        private IElementType myTokenType;
        private LexerPosition myOriginalPosition;
        private int myOldState;

        public MyLexerPosition(final int offset, final IElementType tokenType, final LexerPosition originalPosition, int oldState) {
            myOffset = offset;
            myTokenType = tokenType;
            myOriginalPosition = originalPosition;
            myOldState = oldState;
        }

        public int getOffset() {
            return myOffset;
        }

        public int getState() {
            return myOriginalPosition.getState();
        }

        public IElementType getType() {
            return myTokenType;
        }

        public LexerPosition getOriginalPosition() {
            return myOriginalPosition;
        }

        public int getOldState() {
            return myOldState;
        }
    }
}
