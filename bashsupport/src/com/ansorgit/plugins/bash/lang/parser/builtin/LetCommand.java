package com.ansorgit.plugins.bash.lang.parser.builtin;

import com.ansorgit.plugins.bash.lang.LanguageBuiltins;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticFactory;
import com.ansorgit.plugins.bash.lang.parser.arithmetic.ArithmeticParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * let Argument [Argument ...]
 * Each argument is evaluated as an arithmetic expression
 */
class LetCommand implements ParsingFunction, ParsingTool {
    private TokenSet END_TOKENS = TokenSet.create(SEMI, LINE_FEED);
    @Override
    public boolean isValid(BashPsiBuilder builder) {
        IElementType tokenType = builder.getTokenType();
        String tokenText = builder.getTokenText();
        return tokenType == WORD && LanguageBuiltins.arithmeticCommands.contains(tokenText);
    }

    @Override
    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        //eat the "let" token
        builder.advanceLexer();

        if (builder.getTokenType(true) == WHITESPACE) {

        }


        ArithmeticParsingFunction arithParser = ArithmeticFactory.entryPoint();
        boolean ok = false;

        while (builder.getTokenType(true) == WHITESPACE) {
            //builder.advanceLexer(true);

            ok = arithParser.parse(builder);
        }

        if (ok) {
            marker.done(GENERIC_COMMAND_ELEMENT);
        } else {
            marker.drop();
            builder.error("Expected an arithmetic expression");
        }

        return ok;
    }
}
