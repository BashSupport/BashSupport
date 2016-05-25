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
package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.embedding.MasqueradingLexer;
import com.intellij.lang.ASTNode;
import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.impl.DelegateMarker;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * PsiBuilder which preprocesses the original text and works on the changed text.
 * The original positions are maintained.
 * The text processing may only remove characters. It must not add any new characters.
 * Removed characters will be output as whitespace to the object using this PsiBuilde
 * <br>
 * === This is a copy of {@link com.intellij.embedding.MasqueradingPsiBuilderAdapter}. ===
 * <br>
 * A delegate PsiBuilder that hides or substitutes some tokens (namely, the ones provided by {@link MasqueradingLexer})
 * from a parser, however, _still inserting_ them into a production tree in their initial appearance.
 *
 * @see MasqueradingLexer
 */
public class UnescapingPsiBuilder extends PsiBuilderAdapter {
    private final static Logger LOG = Logger.getInstance(UnescapingPsiBuilder.class);
    private final PsiBuilderImpl myBuilderDelegate;
    private final Lexer myLexer;
    private final TextPreprocessor textProcessor;
    private final CharSequence processedText;
    private List<MyShiftedToken> myShrunkSequence;
    private int myShrunkSequenceSize;
    private CharSequence myShrunkCharSequence;
    private int myLexPosition;
    private IElementType currentRemapped;
    private ITokenTypeRemapper remapper;

    public UnescapingPsiBuilder(@NotNull final Project project,
                                @NotNull final ParserDefinition parserDefinition,
                                @NotNull final Lexer lexer,
                                @NotNull final ASTNode chameleon,
                                @NotNull final CharSequence originalText,
                                @NotNull final CharSequence processedText,
                                @NotNull final TextPreprocessor textProcessor) {
        this(new PsiBuilderImpl(project, parserDefinition, lexer, chameleon, originalText), textProcessor, processedText);
    }

    private UnescapingPsiBuilder(PsiBuilderImpl builder, TextPreprocessor textProcessor, CharSequence processedText) {
        super(builder);
        this.textProcessor = textProcessor;
        this.processedText = processedText;

        LOG.assertTrue(myDelegate instanceof PsiBuilderImpl);
        myBuilderDelegate = ((PsiBuilderImpl) myDelegate);

        myLexer = myBuilderDelegate.getLexer();

        initShrunkSequence();
    }

    @Override
    public CharSequence getOriginalText() {
        return myShrunkCharSequence;
    }

    @Override
    public void advanceLexer() {
        myLexPosition++;
        skipWhitespace();

        synchronizePositions(false);
    }

    /**
     * @param exact if true then positions should be equal;
     *              else delegate should be behind, not including exactly all foreign (skipped) or whitespace tokens
     */
    private void synchronizePositions(boolean exact) {
        final PsiBuilder delegate = getDelegate();

        if (myLexPosition >= myShrunkSequenceSize || delegate.eof()) {
            myLexPosition = myShrunkSequenceSize;
            while (!delegate.eof()) {
                delegate.advanceLexer();
            }
            return;
        }

        if (delegate.getCurrentOffset() > myShrunkSequence.get(myLexPosition).realStart) {
            LOG.debug("delegate is ahead of my builder!");
            return;
        }

        final int keepUpPosition = getKeepUpPosition(exact);

        while (!delegate.eof()) {
            final int delegatePosition = delegate.getCurrentOffset();

            if (delegatePosition < keepUpPosition) {
                delegate.advanceLexer();
            } else {
                break;
            }
        }
    }

    private int getKeepUpPosition(boolean exact) {
        if (exact) {
            return myShrunkSequence.get(myLexPosition).realStart;
        }

        int lexPosition = myLexPosition;
        while (lexPosition > 0 && (myShrunkSequence.get(lexPosition - 1).shrunkStart == myShrunkSequence.get(lexPosition).shrunkStart
                || isWhiteSpaceOnPos(lexPosition - 1))) {
            lexPosition--;
        }
        if (lexPosition == 0) {
            return myShrunkSequence.get(lexPosition).realStart;
        }
        return myShrunkSequence.get(lexPosition - 1).realStart + 1;
    }

    @Override
    public IElementType lookAhead(int steps) {
        if (eof()) {    // ensure we skip over whitespace if it's needed
            return null;
        }
        int cur = myLexPosition;

        while (steps > 0) {
            ++cur;
            while (cur < myShrunkSequenceSize && isWhiteSpaceOnPos(cur)) {
                cur++;
            }

            steps--;
        }

        return cur < myShrunkSequenceSize ? myShrunkSequence.get(cur).elementType : null;
    }

    @Override
    public IElementType rawLookup(int steps) {
        int cur = myLexPosition + steps;
        return cur >= 0 && cur < myShrunkSequenceSize ? myShrunkSequence.get(cur).elementType : null;
    }

    @Override
    public int rawTokenTypeStart(int steps) {
        int cur = myLexPosition + steps;
        if (cur < 0) {
            return -1;
        }
        if (cur >= myShrunkSequenceSize) {
            return getOriginalText().length();
        }
        return myShrunkSequence.get(cur).shrunkStart;
    }

    @Override
    public int rawTokenIndex() {
        return myLexPosition;
    }

    @Override
    public int getCurrentOffset() {
        return myLexPosition < myShrunkSequenceSize ? myShrunkSequence.get(myLexPosition).shrunkStart : myShrunkCharSequence.length();
    }

    @Override
    public void remapCurrentToken(IElementType type) {
        currentRemapped = type;
    }

    @Override
    public void setTokenTypeRemapper(ITokenTypeRemapper remapper) {
        this.remapper = remapper;
        super.setTokenTypeRemapper(remapper);
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        if (allIsEmpty()) {
            return TokenType.DUMMY_HOLDER;
        }

        skipWhitespace();

        if (currentRemapped != null) {
            IElementType result = currentRemapped;
            currentRemapped = null;
            //replace in the sequence?
            return result;
        }

        IElementType result = myLexPosition < myShrunkSequenceSize ? myShrunkSequence.get(myLexPosition).elementType : null;

        if (remapper != null && result != null) {
            String tokenText = getTokenText();
            int offset = getCurrentOffset();
            int end = offset + (tokenText != null ? tokenText.length() : 0);
            return remapper.filter(result, offset, end, tokenText);
        }

        return result;
    }

    @Nullable
    @Override
    public String getTokenText() {
        if (allIsEmpty()) {
            return getDelegate().getOriginalText().toString();
        }

        skipWhitespace();

        if (myLexPosition >= myShrunkSequenceSize) {
            return null;
        }

        final MyShiftedToken token = myShrunkSequence.get(myLexPosition);
        return myShrunkCharSequence.subSequence(token.shrunkStart, token.shrunkEnd).toString();
    }

    @Override
    public boolean eof() {
        boolean isEof = myLexPosition >= myShrunkSequenceSize;
        if (!isEof) {
            return false;
        }

        synchronizePositions(true);
        return true;
    }

    @NotNull
    @Override
    public Marker mark() {
        // In the case of the topmost node all should be inserted
        if (myLexPosition != 0) {
            synchronizePositions(true);
        }

        final Marker mark = super.mark();
        return new MyMarker(mark, myLexPosition);
    }

    private boolean allIsEmpty() {
        return myShrunkSequenceSize == 0 && getDelegate().getOriginalText().length() != 0;
    }

    private void skipWhitespace() {
        while (myLexPosition < myShrunkSequenceSize && isWhiteSpaceOnPos(myLexPosition)) {
            myLexPosition++;
        }
    }

    private boolean isWhiteSpaceOnPos(int pos) {
        return myBuilderDelegate.whitespaceOrComment(myShrunkSequence.get(pos).elementType);
    }

    protected void initShrunkSequence() {
        initTokenListAndCharSequence(myLexer);
        myLexPosition = 0;
    }

    private void initTokenListAndCharSequence(Lexer lexer) {
        lexer.start(processedText);

        myShrunkSequence = new ArrayList<MyShiftedToken>(512); //assume a larger token size by default
        StringBuilder charSequenceBuilder = new StringBuilder();

        int realPos = 0;
        int shrunkPos = 0;
        while (lexer.getTokenType() != null) {
            final IElementType tokenType = lexer.getTokenType();
            final String tokenText = lexer.getTokenText();

            int tokenStart = lexer.getTokenStart();
            int tokenEnd = lexer.getTokenEnd();
            int realLength = tokenEnd - tokenStart;

            int delta = textProcessor.getContentRange().getStartOffset();
            int originalStart = textProcessor.getOffsetInHost(tokenStart - delta);
            int originalEnd = textProcessor.getOffsetInHost(tokenEnd - delta);

            if (textProcessor.containsRange(tokenStart, tokenEnd) && originalStart != -1 && originalEnd != -1) {
                realLength = originalEnd - originalStart;
                int masqueLength = tokenEnd - tokenStart;

                myShrunkSequence.add(new MyShiftedToken(tokenType,
                        realPos, realPos + realLength,
                        shrunkPos, shrunkPos + masqueLength, tokenText));
                charSequenceBuilder.append(tokenText);

                shrunkPos += masqueLength;
            } else {
                myShrunkSequence.add(new MyShiftedToken(tokenType,
                        realPos, realPos + realLength,
                        shrunkPos, shrunkPos + realLength, tokenText));
                charSequenceBuilder.append(tokenText);

                shrunkPos += realLength;
            }

            realPos += realLength;

            lexer.advance();
        }

        myShrunkCharSequence = charSequenceBuilder.toString();
        myShrunkSequenceSize = myShrunkSequence.size();
    }

    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend", "UnusedDeclaration"})
    private void logPos() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nmyLexPosition=" + myLexPosition + "/" + myShrunkSequenceSize);
        if (myLexPosition < myShrunkSequenceSize) {
            final MyShiftedToken token = myShrunkSequence.get(myLexPosition);
            sb.append("\nshrunk:" + token.shrunkStart + "," + token.shrunkEnd);
            sb.append("\nreal:" + token.realStart + "," + token.realEnd);
            sb.append("\nTT:" + getTokenText());
        }
        sb.append("\ndelegate:");
        sb.append("eof=" + myDelegate.eof());
        if (!myDelegate.eof()) {
            //noinspection ConstantConditions
            sb.append("\nposition:" + myDelegate.getCurrentOffset() + "," + (myDelegate.getCurrentOffset() + myDelegate.getTokenText().length()));
            sb.append("\nTT:" + myDelegate.getTokenText());
        }
        LOG.info(sb.toString());
    }


    private static class MyShiftedToken {
        public final IElementType elementType;

        public final int realStart;
        public final int realEnd;

        public final int shrunkStart;
        public final int shrunkEnd;
        private final String tokenText;

        public MyShiftedToken(IElementType elementType, int realStart, int realEnd, int shrunkStart, int shrunkEnd, String tokenText) {
            this.elementType = elementType;
            this.realStart = realStart;
            this.realEnd = realEnd;
            this.shrunkStart = shrunkStart;
            this.shrunkEnd = shrunkEnd;
            this.tokenText = tokenText;
        }

        @Override
        public String toString() {
            return "MSTk: [" + realStart + ", " + realEnd + "] -> [" + shrunkStart + ", " + shrunkEnd + "]: " + elementType.toString() + " | " + tokenText;
        }
    }

    private class MyMarker extends DelegateMarker {
        private final int myBuilderPosition;

        public MyMarker(Marker delegate, int builderPosition) {
            super(delegate);

            myBuilderPosition = builderPosition;
        }

        @Override
        public void rollbackTo() {
            super.rollbackTo();
            myLexPosition = myBuilderPosition;
        }

        @Override
        public void doneBefore(@NotNull IElementType type, @NotNull Marker before) {
            super.doneBefore(type, getDelegateOrThis(before));
        }

        @Override
        public void doneBefore(@NotNull IElementType type, @NotNull Marker before, String errorMessage) {
            super.doneBefore(type, getDelegateOrThis(before), errorMessage);
        }

        @Override
        public void done(@NotNull IElementType type) {
            super.done(type);
        }

        @NotNull
        private Marker getDelegateOrThis(@NotNull Marker marker) {
            if (marker instanceof DelegateMarker) {
                return ((DelegateMarker) marker).getDelegate();
            } else {
                return marker;
            }
        }
    }
}
