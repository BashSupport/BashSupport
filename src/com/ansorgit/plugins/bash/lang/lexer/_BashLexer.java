package com.ansorgit.plugins.bash.lang.lexer;

import com.ansorgit.plugins.bash.lang.BashVersion;
import com.ansorgit.plugins.bash.util.IntStack;

/**
 *
 */
public final class _BashLexer extends _BashLexerBase implements BashLexerDef {
    private final IntStack lastStates = new IntStack(25);
    //Help data to parse (nested) strings.
    private final StringLexingstate string = new StringLexingstate();
    //parameter expansion parsing state
    boolean paramExpansionHash = false;
    boolean paramExpansionWord = false;
    boolean paramExpansionOther = false;
    private int openParenths = 0;
    private boolean isBash4 = false;
    //True if the parser is in the case body. Necessary for proper lexing of the IN keyword
    private boolean inCaseBody = false;
    //conditional expressions
    private boolean emptyConditionalCommand = false;
    private HeredocLexingState heredocLexingState = new HeredocLexingState();

    public _BashLexer(BashVersion version, java.io.Reader in) {
        super(in);

        this.isBash4 = BashVersion.Bash_v4.equals(version);
    }

    @Override
    public boolean isHeredocEnd(String text) {
        return heredocLexingState.isNextHeredocMarker(text);
    }

    @Override
    public boolean isHeredocEvaluating() {
        return heredocLexingState.isExpectingEvaluatingHeredoc();
    }

    @Override
    public void pushExpectedHeredocMarker(CharSequence expectedHeredocMarker) {
        this.heredocLexingState.pushHeredocMarker(expectedHeredocMarker.toString());
    }

    @Override
    public void popHeredocMarker(CharSequence marker) {
        heredocLexingState.popHeredocMarker(marker.toString());
    }

    @Override
    public boolean isHeredocMarkersEmpty() {
        return heredocLexingState.isEmpty();
    }
    
    @Override
    public boolean isEmptyConditionalCommand() {
        return emptyConditionalCommand;
    }

    @Override
    public void setEmptyConditionalCommand(boolean emptyConditionalCommand) {
        this.emptyConditionalCommand = emptyConditionalCommand;
    }

    @Override
    public StringLexingstate stringParsingState() {
        return string;
    }

    @Override
    public boolean isInCaseBody() {
        return inCaseBody;
    }

    @Override
    public void setInCaseBody(boolean inCaseBody) {
        this.inCaseBody = inCaseBody;
    }

    @Override
    public boolean isBash4() {
        return isBash4;
    }

    /**
     * Goes to the given state and stores the previous state on the stack of states.
     * This makes it possible to have several levels of lexing, e.g. for $(( 1+ $(echo 3) )).
     */
    public void goToState(int newState) {
        lastStates.push(yystate());
        yybegin(newState);
    }

    /**
     * Goes back to the previous state of the lexer. If there
     * is no previous state then YYINITIAL, the initial state, is chosen.
     */
    public void backToPreviousState() {
        // pop() will throw an exception if empty
        yybegin(lastStates.pop());
    }

    @Override
    public boolean isInState(int state) {
        return lastStates.contains(state);
    }

    @Override
    public int openParenthesisCount() {
        return openParenths;
    }

    @Override
    public void incOpenParenthesisCount() {
        openParenths++;
    }

    @Override
    public void decOpenParenthesisCount() {
        openParenths--;
    }

    @Override
    public boolean isParamExpansionWord() {
        return paramExpansionWord;
    }

    @Override
    public void setParamExpansionWord(boolean paremeterExpansionWord) {
        this.paramExpansionWord = paremeterExpansionWord;
    }

    @Override
    public boolean isParamExpansionOther() {
        return paramExpansionOther;
    }

    @Override
    public void setParamExpansionOther(boolean paremeterExpansionOther) {
        this.paramExpansionOther = paremeterExpansionOther;
    }

    @Override
    public boolean isParamExpansionHash() {
        return paramExpansionHash;
    }

    @Override
    public void setParamExpansionHash(boolean paremeterExpansionHash) {
        this.paramExpansionHash = paremeterExpansionHash;
    }
}
