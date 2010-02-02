/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: MergeTuple.java, Class: MergeTuple
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

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * A single mapping of a set of tokens to a target element.
 * <p/>
 * User: jansorg
 * Date: 28.07.2009
 * Time: 18:59:05
 */
public class MergeTuple {
    private final TokenSet tokensToMerge;
    private final IElementType targetType;

    public static MergeTuple create(TokenSet tokensToMerge, IElementType targetType) {
        return new MergeTuple(tokensToMerge, targetType);
    }

    private MergeTuple(TokenSet tokensToMerge, IElementType targetType) {
        this.tokensToMerge = tokensToMerge;
        this.targetType = targetType;
    }

    public TokenSet getTokensToMerge() {
        return tokensToMerge;
    }

    public IElementType getTargetType() {
        return targetType;
    }
}
