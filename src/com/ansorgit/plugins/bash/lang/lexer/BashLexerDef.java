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

    void popStates(int lastStateToPop);

    boolean isInState(int state);

    int openParenthesisCount();

    void incOpenParenthesisCount();

    void decOpenParenthesisCount();

    boolean isParamExpansionHash();

    void setParamExpansionHash(boolean paramExpansionHash);

    boolean isParamExpansionWord();

    void setParamExpansionWord(boolean paramExpansionWord);

    boolean isParamExpansionOther();

    void setParamExpansionOther(boolean paramExpansionOther);

    boolean isInCaseBody();

    void setInCaseBody(boolean inCaseBody);

    StringLexingstate stringParsingState();

    boolean isEmptyConditionalCommand();

    void setEmptyConditionalCommand(boolean emptyConditionalCommand);

    HeredocLexingState heredocState();

    boolean isInHereStringContent();

    void enterHereStringContent();

    void leaveHereStringContent();
}
