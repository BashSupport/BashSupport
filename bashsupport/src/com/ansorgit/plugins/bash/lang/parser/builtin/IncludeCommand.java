package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.google.common.collect.Sets;
import com.intellij.lang.PsiBuilder;

import java.util.Set;

/**
 * User: jansorg
 * Date: 18.02.11
 * Time: 19:37
 */
public class IncludeCommand implements ParsingFunction, ParsingTool {
    private final Set<String> acceptedCommands = Sets.newHashSet(".", "source");

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == BashTokenTypes.INTERNAL_COMMAND
                && acceptedCommands.contains(builder.getTokenText());
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();

        //eat the "." or "source" part
        builder.advanceLexer();

        //parse the file reference
        PsiBuilder.Marker fileMarker = builder.mark();
        boolean wordResult = Parsing.word.parseWord(builder, false);
        if (!wordResult) {
            fileMarker.drop();
            mark.drop();
            builder.error("Expected file name");
            return false;
        }

        fileMarker.done(FILE_REFERENCE);

        while (Parsing.word.isWordToken(builder)) {
            Parsing.word.parseWord(builder);
        }

        //optional parameters

        mark.done(INCLUDE_COMMAND_ELEMENT);

        return true;
    }
}
