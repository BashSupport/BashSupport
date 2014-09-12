package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.command.CommandParsingUtil;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;

public class GetOptsCommand implements ParsingFunction {
    @Override
    public boolean isValid(BashPsiBuilder builder) {
        return "getopts".equals(builder.getTokenText());
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        if (!isValid(builder)) {
            return false;
        }

        PsiBuilder.Marker cmdMarker = builder.mark();

        //eat the getopts and mark it
        PsiBuilder.Marker getOpts = builder.mark();
        builder.advanceLexer();
        getOpts.done(GENERIC_COMMAND_ELEMENT);

        //the first option is the option definition
        if (Parsing.word.isComposedString(builder.getTokenType())) {
            if (!Parsing.word.parseComposedString(builder)) {
                cmdMarker.drop();
                return false;
            }
        } else if (ParserUtil.isWordToken(builder.getTokenType())) {
            builder.advanceLexer();
        } else {
            cmdMarker.drop();
            builder.error("Expected the getopts option string");
            return false;
        }

        //the second argument is the variable name, i.e. the defined variable
        boolean varDefRead = CommandParsingUtil.readAssignment(builder, CommandParsingUtil.Mode.SimpleMode, true, false);
        if (!varDefRead) {
            cmdMarker.drop();
            builder.error("Expected getops variable name");
            return false;
        }

        //now read all remaining arguments in the command, these are the arguments parsed by getopts
        if (Parsing.word.isWordToken(builder)) {
            if (!Parsing.word.parseWordList(builder, false, false)) {
                cmdMarker.drop();
                return false;
            }
        }

        cmdMarker.done(SIMPLE_COMMAND_ELEMENT);

        return true;
    }
}
