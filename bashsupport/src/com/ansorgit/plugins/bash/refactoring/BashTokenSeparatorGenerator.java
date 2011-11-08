/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTokenSeparatorGenerator.java, Class: BashTokenSeparatorGenerator
 * Last modified: 2010-12-10 18:30
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

package com.ansorgit.plugins.bash.refactoring;

import com.intellij.lang.ASTNode;
import com.intellij.lang.TokenSeparatorGenerator;

/**
 * The token separator generator is called for PSI element replacements, e.g. during a rename refactoring.
 * We do not want to insert extra separators (e.g. whitespace) between tokens so we always return null.
 * <p/>
 * User: jansorg
 * Date: 10.12.10
 * Time: 18:01
 */
public class BashTokenSeparatorGenerator implements TokenSeparatorGenerator {
    public ASTNode generateWhitespaceBetweenTokens(ASTNode left, ASTNode right) {
        return null;
    }
}
