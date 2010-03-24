/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTokenRemapper.java, Class: BashTokenRemapper
 * Last modified: 2010-03-24
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Token remapper for the BashPsiBuilder.
 * It remapps certain tokens to words if remapping is enabled in the PsiBuilder
 * instance. The remapping is activated by the parsing functions.
 * <p/>
 * Date: 12.04.2009
 * Time: 16:25:28
 *
 * @author Joachim Ansorg
 */
class BashTokenRemapper implements ITokenTypeRemapper, BashTokenTypes {
    //private static final Logger log = Logger.getInstance("#bash.BashTokenRemapper");
    private final BashPsiBuilder builder;
    private final TokenSet mappedToWord = TokenSet.create(
            ASSIGNMENT_WORD, ARRAY_ASSIGNMENT_WORD,
            LEFT_SQUARE, BANG_TOKEN,
            IF_KEYWORD, THEN_KEYWORD, ELIF_KEYWORD, ELSE_KEYWORD, FI_KEYWORD,
            WHILE_KEYWORD, DO_KEYWORD, DONE_KEYWORD,
            EQ, AT);

    private boolean remapShebangToComment = false;


    public BashTokenRemapper(final BashPsiBuilder builder) {
        this.builder = builder;
    }

    public IElementType filter(final IElementType elementType, final int from, final int to, final CharSequence charSequence) {
        //we have to remap because commands like "echo a=b" are valid and this is not an assignment command
        if (builder.getParsingState().isInSimpleCommand() && remappedToWord(elementType)) {
            return WORD;
        }

        if (builder.getParsingState().isInHereDoc() && remappedInHereDoc(elementType)) {
            return WORD;
        }

        if (remapShebangToComment && elementType == SHEBANG) {
            return COMMENT;
        }

        return elementType;
    }

    private boolean remappedInHereDoc(IElementType elementType) {
        boolean evaluating = builder.getHereDocData().isCurrentlyEvaluating();
        if (elementType == WHITESPACE || evaluating && elementType == BashTokenTypes.VARIABLE) {
            return false;
        }

        return elementType != BashTokenTypes.LINE_FEED;
    }

    private boolean remappedToWord(final IElementType element) {
        return mappedToWord.contains(element);
    }

    public void setMapShebangToComment(boolean remapToComment) {
        this.remapShebangToComment = remapToComment;
    }
}
