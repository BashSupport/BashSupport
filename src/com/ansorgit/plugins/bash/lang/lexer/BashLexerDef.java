package com.ansorgit.plugins.bash.lang.lexer;

import com.intellij.lexer.FlexLexer;

/**
 */
public interface BashLexerDef extends BashTokenTypes, FlexLexer {
    boolean isBash4();

    /**
     * Goes to the given state and stores the previous state on the stack of states.
     * This makes it possible to have several levels of lexing, e.g. for $(( 1+ $(echo 3) )).
     */
    void goToState(int newState);

    /**
     * Goes back to the previous state of the lexer. If there
     * is no previous state then YYINITIAL, the initial state, is chosen.
     */
    void backToPreviousState();

    int openParenthesisCount();

    void incOpenParenthesisCount();

    void decOpenParenthesisCount();

    boolean isParamExpansionHash();

    void setParamExpansionHash(boolean paremeterExpansionHash);

    boolean isParamExpansionWord();

    void setParamExpansionWord(boolean paremeterExpansionWord);

    boolean isParamExpansionOther();

    void setParamExpansionOther(boolean paremeterExpansionOther);

    boolean isInCaseBody();

    void setInCaseBody(boolean inCaseBody);

    StringParsingState stringParsingState();

    boolean isEmptyConditionalCommand();

    void setEmptyConditionalCommand(boolean emptyConditionalCommand);

    boolean isExpectArithExpression();

    void setExpectArithExpression(boolean expectArithExpression);

    boolean isStartNewArithExpression();

    void setStartNewArithExpression(boolean startNewArithExpression);

    void setExpectedHeredocMarker(CharSequence marker);

    CharSequence getExpectedHeredocMarker();

    boolean isHeredocEnd(String text);
}
