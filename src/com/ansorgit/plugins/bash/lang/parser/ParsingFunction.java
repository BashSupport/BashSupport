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

/**
 * A parsing function provides a common interface to parse a single aspect of the grammar.
 * <br>
 * @author jansorg
 */
public interface ParsingFunction extends BashTokenTypes, BashElementTypes {
    /**
     * Returns whether the next tokens of the psi builder form a valid sequence understood
     * by the parser.
     *
     * @param builder The provider of the psi tokens
     * @return True if the next tokens are valid, false if the sequence is invalid.
     */
    boolean isValid(BashPsiBuilder builder);

    default boolean isInvalid(BashPsiBuilder builder) {
        return !isValid(builder);
    }

    /**
     * Parse the next few tokens. If the next tokens could not be parsed false is returned.
     *
     * @param builder The provider of the tokens.
     * @return True if the sequence of tokens was understood by the parser. False if it was invalid.
     */
    boolean parse(BashPsiBuilder builder);
}
