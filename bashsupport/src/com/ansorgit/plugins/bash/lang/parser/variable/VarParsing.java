/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: VarParsing.java, Class: VarParsing
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

package com.ansorgit.plugins.bash.lang.parser.variable;

import com.ansorgit.plugins.bash.lang.parser.ParsingChain;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;

/**
 * Parsing of variables. Includes $(), ${} and $(()).
 * <p/>
 * Date: 27.03.2009
 * Time: 11:07:41
 *
 * @author Joachim Ansorg
 */
public class VarParsing extends ParsingChain implements ParsingTool {
    public VarParsing() {
        addParsingFunction(new SimpleVarParsing());
        addParsingFunction(new ComposedVariableParsing());
    }
}
