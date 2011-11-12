/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashBraceMatcher.java, Class: BashBraceMatcher
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

package com.ansorgit.plugins.bash.editor.highlighting;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Braces matcher for bash files. Referenced by the plugin.xml file.
 * <p/>
 * Date: 23.03.2009
 * Time: 15:06:18
 *
 * @author Joachim Ansorg
 */
public class BashBraceMatcher implements PairedBraceMatcher {
    private static final BracePair[] PAIRS = new BracePair[]{
            new BracePair(BashTokenTypes.LEFT_PAREN, BashTokenTypes.RIGHT_PAREN, false),
            new BracePair(BashTokenTypes.LEFT_SQUARE, BashTokenTypes.RIGHT_SQUARE, false),
            new BracePair(BashTokenTypes.EXPR_ARITH, BashTokenTypes._EXPR_ARITH, true),
            new BracePair(BashTokenTypes.EXPR_CONDITIONAL, BashTokenTypes._EXPR_CONDITIONAL, false),
            new BracePair(BashTokenTypes.STRING_BEGIN, BashTokenTypes.STRING_END, false),
            new BracePair(BashTokenTypes.LEFT_CURLY, BashTokenTypes.RIGHT_CURLY, true), //structural
    };

    public BracePair[] getPairs() {
        return PAIRS;
    }

    public boolean isPairedBracesAllowedBeforeType(@NotNull final IElementType lbraceType, @Nullable final IElementType tokenType) {
        return BashTokenTypes.WHITESPACE == tokenType
                || BashTokenTypes.commentTokens.contains(tokenType)
                || tokenType == BashTokenTypes.SEMI
                || tokenType == BashTokenTypes.COMMA
                || tokenType == BashTokenTypes.RIGHT_PAREN
                || tokenType == BashTokenTypes.RIGHT_SQUARE
                || tokenType == BashTokenTypes.RIGHT_CURLY
                || null == tokenType;
    }

    public int getCodeConstructStart(final PsiFile file, int openingBraceOffset) {
        return openingBraceOffset;
    }
}
