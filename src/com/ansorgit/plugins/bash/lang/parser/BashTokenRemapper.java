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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lang.ITokenTypeRemapper;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Token remapper for the BashPsiBuilder.
 * It remapps certain tokens to words if remapping is enabled in the PsiBuilder
 * instance. The remapping is activated by the parsing functions.
 * <br>
 * @author jansorg
 */
final class BashTokenRemapper implements ITokenTypeRemapper, BashTokenTypes {
    private static final TokenSet mappedToWord = TokenSet.create(
            ASSIGNMENT_WORD,
            LEFT_SQUARE, RIGHT_SQUARE,
            IF_KEYWORD, THEN_KEYWORD, ELIF_KEYWORD, ELSE_KEYWORD, FI_KEYWORD,
            WHILE_KEYWORD, DO_KEYWORD, DONE_KEYWORD,
            FOR_KEYWORD,
            FUNCTION_KEYWORD,
            CASE_KEYWORD, ESAC_KEYWORD,
            SELECT_KEYWORD,
            UNTIL_KEYWORD,
            TIME_KEYWORD,
            BRACKET_KEYWORD, _BRACKET_KEYWORD,
            EQ,
            AT);

    private final BashPsiBuilder builder;
    private boolean remapShebangToComment = false;

    public BashTokenRemapper(final BashPsiBuilder builder) {
        this.builder = builder;
    }

    public IElementType filter(final IElementType elementType, final int from, final int to, final CharSequence charSequence) {
        if (remapShebangToComment && elementType == SHEBANG) {
            return COMMENT;
        }

        //we have to remap because commands like "echo a=b" are valid and this is not an assignment command
        if (builder.getParsingState().isInSimpleCommand() && mappedToWord.contains(elementType)) {
            return WORD;
        }

        return elementType;
    }

    public void enableShebangToCommentMapping() {
        this.remapShebangToComment = true;
    }
}
