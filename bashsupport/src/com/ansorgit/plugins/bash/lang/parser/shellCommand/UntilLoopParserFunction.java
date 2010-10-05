/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: UntilLoopParserFunction.java, Class: UntilLoopParserFunction
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;

/**
 * Parsing function for until loops.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:11:11
 *
 * @author Joachim Ansorg
 */
public class UntilLoopParserFunction extends AbstractLoopParser {
    public UntilLoopParserFunction() {
        super(UNTIL_KEYWORD, BashElementTypes.UNTIL_COMMAND);
    }
}