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

package com.ansorgit.plugins.bash.lang.parser.builtin.varDef;

import com.ansorgit.plugins.bash.lang.parser.ParsingTool;

import java.util.Arrays;

/**
 * Syntax: mapfile [-d delim] [-n count] [-O origin] [-s count] [-t] [-u fd] [-C callback] [-c quantum] [array]
 * <br>
 * Parses the mapfile command.
 * <br>
 *
 * @author jansorg
 */
class MapfileCommand extends AbstractVariableDefParsing implements ParsingTool {
    //arguments which take a value after this
    //fixme this needs to be improved as values may span more than one token
    private static final int[] VALUE_ARGS = new int[]{'d', 'n', 'O', 's', 'u', 'C', 'c'};

    public MapfileCommand() {
        this("mapfile");
    }

    MapfileCommand(String commandText) {
        super(false, GENERIC_COMMAND_ELEMENT, commandText, false, false);
    }

    @Override
    boolean argumentValueExpected(String name) {
        return name.chars().anyMatch(value -> Arrays.stream(VALUE_ARGS).anyMatch(c -> value == c));
    }
}
