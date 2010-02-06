/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ArithmeticExprParser.java, Class: ArithmeticExprParser
 * Last modified: 2010-02-06
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
 * User: jansorg
 * Date: Feb 6, 2010
 * Time: 4:27:55 PM
 */
public class ArithmeticExprParser extends AbstractRepeatedExpr {
    public static final ParsingFunction instance = new ArithmeticExprParser();

    public ArithmeticExprParser() {
        super(new CombinationAssignment(), COMMA);
    }
}
