/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ExportCommand.java, Class: ExportCommand
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

package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.intellij.psi.tree.IElementType;

/**
 * Syntax: export [-nf] [name[=value] ...] or export -p
 * <p/>
 * Makes the assignments available to the reference detection.
 * <p/>
 * Date: 01.05.2009
 * Time: 20:55:46
 *
 * @author Joachim Ansorg
 */
class ExportCommand extends VariableDefCommand implements ParsingTool {
    ExportCommand() {
        super(true, INTERNAL_COMMAND_ELEMENT, "export", true);
    }

    public boolean isValid(IElementType token) {
        throw new UnsupportedOperationException();
    }
}
