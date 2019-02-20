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

import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.OptionalParseResult;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.google.common.collect.Maps;
import com.intellij.lang.PsiBuilder;

import java.util.Map;

/**
 * This is an optimized parsing chain for variable definition parsing.
 * Subclasses of AbstractVariableDefParsing always created a marker, checked whether the command name is supported
 * and then either rolled back the command or continued parsing if supported.
 * <p>
 * We have 8 subclasses which means that parsing could roll back up to 8 times before the supported command was found.
 * We now lookup the command once and then continue parsing with the command supporting it.
 *
 * @author jansorg
 */
public class BuiltinVarCommandParser implements ParsingFunction {
    private final Map<String, AbstractVariableDefParsing> cmdMapping = Maps.newHashMap();

    public BuiltinVarCommandParser() {
        add(new ExportCommand());
        add(new ReadonlyCommand());
        add(new DeclareCommand());
        add(new MapfileCommand());
        add(new ReadarrayCommand());
        add(new TypesetCommand());
        add(new ReadCommand());
        add(new LocalCommand());
    }

    private void add(AbstractVariableDefParsing cmd) {
        cmdMapping.put(cmd.getCommandName(), cmd);
    }

    @Override
    public boolean isValid(BashPsiBuilder builder) {
        throw new UnsupportedOperationException("use parseIfValid");
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        return parseIfValid(builder).isParsedSuccessfully();
    }

    @Override
    public OptionalParseResult parseIfValid(BashPsiBuilder builder) {
        PsiBuilder.Marker cmdMarker = builder.mark();

        //fixme read assignment and redirects, but return an error if the command isn't supporting it

        OptionalParseResult result = CommandParsingUtil.readAssignmentsAndRedirectsIfValid(builder, false, CommandParsingUtil.Mode.StrictAssignmentMode, true);
        if (result.isValid() && !result.isParsedSuccessfully()) {
            //fixme validate. Shouldn't we return an error here?
            cmdMarker.drop();
            return OptionalParseResult.ParseError;
        }

        //fixme validate token type!
        //fixme perhaps look ahead for token type?
        String cmdName = builder.getTokenText();

        AbstractVariableDefParsing parsingFunction = cmdMapping.get(cmdName);
        if (parsingFunction == null) {
            cmdMarker.rollbackTo();
            return OptionalParseResult.Invalid;
        }

        //fixme optimize
        cmdMarker.rollbackTo();
        cmdMarker = builder.mark();

        result = parsingFunction.parseIfValid(builder);
        if (!result.isParsedSuccessfully()) {
            cmdMarker.drop();
        } else {
            cmdMarker.done(BashElementTypes.SIMPLE_COMMAND_ELEMENT);
        }
        return result;
    }
}
