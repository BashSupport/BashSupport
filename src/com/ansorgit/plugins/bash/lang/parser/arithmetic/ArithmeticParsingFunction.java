/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticParsingFunction.java, Class: ArithmeticParsingFunction
 * Last modified: 2010-07-17
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

package com.ansorgit.plugins.bash.lang.parser.arithmetic;

import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;

/**
 * Extended parsing function for parsing of arithmetic expressions.
 * <p/>
 * User: jansorg
 * Date: Apr 17, 2010
 * Time: 10:47:30 PM
 */
public interface ArithmeticParsingFunction extends ParsingFunction {
    /**
     * Does partial parsing, e.g. after an already parsed parenthesis expression. It
     * starts with an operator.
     *
     * @param builder          The builder to use.
     * @return True if the parsing was successful.
     */
//    boolean partialParsing(BashPsiBuilder builder);

    /**
     * Returns true if the following tokens start with a valid partial expression.
     *
     * @param builder Streams the tokens
     * @return True if partial parsing may be valid.
     */
//    boolean isValidPartial(BashPsiBuilder builder);
}
