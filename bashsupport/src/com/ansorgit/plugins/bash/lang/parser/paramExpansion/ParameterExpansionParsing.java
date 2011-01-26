package com.ansorgit.plugins.bash.lang.parser.paramExpansion;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.BashSmartMarker;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Handles the default parsing of yet unknown / unsupported parameter expansions.
 * <p/>
 * User: jansorg
 * Date: 03.12.10
 * Time: 19:40
 */
public class ParameterExpansionParsing implements ParsingFunction {
    private static final TokenSet validTokens = TokenSet.create(LEFT_SQUARE, RIGHT_SQUARE, PARAM_EXPANSION_OP_UNKNOWN);

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == LEFT_CURLY;
    }

    public boolean parse(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        ParserUtil.getTokenAndAdvance(builder);

        //fixme check token type, only certain tokens are allowed

        if (builder.getTokenType(true) == PARAM_EXPANSION_OP_EXCL) {
            //indirect variable reference
            builder.advanceLexer(true);
        }

        //the first token has to be a plain word token
        BashSmartMarker firstElementMarker = new BashSmartMarker(builder.mark());

        IElementType firstToken = builder.getTokenType(true);
        if (!ParserUtil.isWordToken(firstToken)) {
            firstElementMarker.drop();
            marker.drop();

            return false;
        }

        //the first element is a word token, now check if it is a var use or var def token

        //eat the first token
        builder.advanceLexer(true);
        boolean markedAsVar = false;
        boolean isValid = true;

        if (builder.getTokenType(true) != RIGHT_CURLY) {
            IElementType operator = builder.getTokenType(true);
            if (!paramExpansionOperators.contains(operator)) {
                firstElementMarker.drop();
                marker.drop();
                return false;
            }

            if (paramExpansionAssignmentOps.contains(operator)) {
                //ParserUtil.markTokenAndAdvance(builder, VAR_DEF_ELEMENT);
                firstElementMarker.done(VAR_DEF_ELEMENT);
                builder.advanceLexer(true);
            } else if (paramExpansionOperators.contains(operator)) {
                //unknown operator
                firstElementMarker.done(VAR_ELEMENT);
                builder.advanceLexer(true);
            } else {
                //something else, e.g. indirect variable reference
                firstElementMarker.drop();
            }

            while (isValid && builder.getTokenType() != RIGHT_CURLY) {
                if (Parsing.var.isValid(builder)) {
                    isValid = Parsing.var.parse(builder);
                } else if (Parsing.word.isComposedString(builder.getTokenType())) {
                    isValid = Parsing.word.parseComposedString(builder);
                } else {
                    IElementType next = ParserUtil.getTokenAndAdvance(builder);
                    isValid = validTokens.contains(next) || ParserUtil.isWordToken(next);
                }
            }
        } else {
            firstElementMarker.done(VAR_ELEMENT);
        }

        //make sure the first marker is closed before the closing marker
        if (firstElementMarker.isOpen()) {
            firstElementMarker.drop();
        }

        IElementType endToken = ParserUtil.getTokenAndAdvance(builder);
        boolean validEnd = RIGHT_CURLY == endToken;
        if (validEnd && !markedAsVar) {
            marker.done(PARAM_EXPANSION_ELEMENT);
        } else {
            marker.drop();
        }

        return validEnd && isValid;
    }
}
