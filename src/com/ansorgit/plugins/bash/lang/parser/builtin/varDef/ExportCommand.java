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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Syntax: export [-nf] [name[=value] ...] or export -p
 * <br>
 * Makes the assignments available to the reference detection.
 * <br>
 * @author jansorg
 */
class ExportCommand extends AbstractVariableDefParsing implements ParsingTool {
    ExportCommand() {
        super(true, GENERIC_COMMAND_ELEMENT, "export", true, false);
    }

    @Override
    boolean argumentValueExpected(String name) {
        // -f expects a function name
        return "-f".equals(name);
    }

    @Override
    protected boolean parseArgumentValue(String argName, BashPsiBuilder builder) {
        if ("-f".equals(argName)) {
            IElementType token = builder.getTokenType();
            if (token == STRING2 || token == WORD || Parsing.word.isSimpleComposedString(builder, false)) {
                return parseFunctionName(builder);
            }
        }

        return super.parseArgumentValue(argName, builder);
    }

    private boolean parseFunctionName(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();
        PsiBuilder.Marker markerInner = builder.mark();

        if (Parsing.word.parseWordIfValid(builder).isParsedSuccessfully()) {
            markerInner.done(GENERIC_COMMAND_ELEMENT);
            marker.done(SIMPLE_COMMAND_ELEMENT);
            return true;
        }

        markerInner.drop();
        marker.drop();
        return false;
    }
}

