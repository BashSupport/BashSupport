package com.ansorgit.plugins.bash.lang.parser.eval;

import com.intellij.lexer.DelegateLexer;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrefixSuffixAddingLexer extends DelegateLexer {
    private final String prefix;
    private final IElementType prefixType;
    private final String suffix;
    private final IElementType suffixType;

    boolean afterPrefix = false;
    boolean delegateEOF = false;
    boolean afterEOF = false;

    public PrefixSuffixAddingLexer(@NotNull Lexer delegate, String prefix, IElementType prefixType, String suffix, IElementType suffixType) {
        super(delegate);
        this.prefix = prefix;
        this.prefixType = prefixType;
        this.suffix = suffix;
        this.suffixType = suffixType;
    }

    @Nullable
    @Override
    public IElementType getTokenType() {
        if (afterEOF) {
            return null;
        } else if (!afterPrefix) {
            return prefixType;
        } else if (delegateEOF) {
            return suffixType;
        }

        return myDelegate.getTokenType();
    }

    @Override
    public int getTokenStart() {
        int tokenStart = myDelegate.getTokenStart();
        if (!afterPrefix && tokenStart == 0) {
            return 0;
        }

        if (afterEOF) {
            return prefix.length() + suffix.length() + tokenStart;
        }

        return prefix.length() + tokenStart;
    }

    @Override
    public int getTokenEnd() {
        if (!afterPrefix) {
            return prefix.length();
        }

        int tokenEnd = myDelegate.getTokenEnd();
        if (delegateEOF) {
            return prefix.length() + suffix.length() + tokenEnd;
        }

        return prefix.length() + tokenEnd;
    }

    @Override
    public int getBufferEnd() {
        //fixme check with specific lexing range
        return myDelegate.getBufferEnd() + prefix.length() + suffix.length();
    }

    @NotNull
    @Override
    public LexerPosition getCurrentPosition() {
        final int start = getTokenStart();
        final int state = getState();

        return new LexerPosition() {
            @Override
            public int getOffset() {
                return start;
            }

            @Override
            public int getState() {
                return state;
            }
        };
    }

    @Override
    public void advance() {
        if (afterPrefix) {
            myDelegate.advance();
        }

        afterPrefix = true;
        afterEOF |= delegateEOF;
        delegateEOF |= myDelegate.getTokenType() == null;
    }

    @NotNull
    @Override
    public String getTokenText() {
        return getTokenSequence().toString();
    }

    @NotNull
    @Override
    public CharSequence getTokenSequence() {
        if (afterEOF) {
            return "";
        }

        if (!afterPrefix) {
            return prefix;
        }

        if (delegateEOF) {
            return suffix;
        }

        return myDelegate.getTokenSequence();
    }

    @Override
    public void restore(@NotNull final LexerPosition position) {
        final int prefixLength = prefix.length();

        final int newOffset = position.getOffset() - prefixLength;
        if (newOffset == 0) {
            this.afterPrefix = false;
        }

        if (newOffset < myDelegate.getBufferEnd()) {
            this.delegateEOF = false;
            this.afterEOF = false;
        }

        myDelegate.restore(new LexerPosition() {
            @Override
            public int getOffset() {
                return newOffset;
            }

            @Override
            public int getState() {
                return position.getState();
            }
        });
    }

    @Override
    public void start(@NotNull CharSequence buffer, int startOffset, int endOffset, int initialState) {
        myDelegate.start(buffer, startOffset, endOffset, initialState);

        this.afterPrefix = false;
        this.delegateEOF = false;
        this.afterEOF = false;
    }
}
