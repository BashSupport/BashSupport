package com.ansorgit.plugins.bash.lang.parser.paramExpansion;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.BashSmartMarker;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.google.common.base.Function;
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
    private static final TokenSet validTokens = TokenSet.orSet(TokenSet.create(PARAM_EXPANSION_OP_UNKNOWN), paramExpansionOperators);
    private static final TokenSet prefixlessExpansionsOperators = TokenSet.create(PARAM_EXPANSION_OP_LENGTH);

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

        IElementType firstToken = builder.getTokenType(true);

        //some tokens, like the length expansion '#' must not have a prefixes word character
        if (prefixlessExpansionsOperators.contains(firstToken)) {
            return parsePrefixlessExpansion(builder, marker);
        }

        //the first token has to be a plain word token
        BashSmartMarker firstElementMarker = new BashSmartMarker(builder.mark());

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

            //array reference
            if (operator == LEFT_SQUARE) {
                //one of x[*] and x[@]
                boolean isSpecialReference = ParserUtil.hasNextTokens(builder, LEFT_SQUARE, PARAM_EXPANSION_OP_AT, RIGHT_SQUARE)
                        || ParserUtil.hasNextTokens(builder, LEFT_SQUARE, PARAM_EXPANSION_OP_STAR, RIGHT_SQUARE);

                boolean isValidReference = ParserUtil.checkAndRollback(builder, new Function<BashPsiBuilder, Boolean>() {
                    public Boolean apply(BashPsiBuilder builder) {
                        return Parsing.shellCommand.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                    }
                });

                if (isSpecialReference || isValidReference) {
                    firstElementMarker.done(VAR_ELEMENT);
                }

                //now parse the reference in the square brackets
                if (isSpecialReference) {
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                    ParserUtil.getTokenAndAdvance(builder);
                } else {
                    boolean validArrayReference = Parsing.shellCommand.arithmeticParser.parse(builder, LEFT_SQUARE, RIGHT_SQUARE);
                    if (!validArrayReference) {
                        firstElementMarker.drop();
                        marker.drop();
                        return false;
                    }
                }
            } else {
                if (!paramExpansionOperators.contains(operator)) {
                    firstElementMarker.drop();

                    marker.drop();
                    return false;
                }

                if (paramExpansionAssignmentOps.contains(operator)) {
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

    private boolean parsePrefixlessExpansion(BashPsiBuilder builder, PsiBuilder.Marker marker) {
        //eat the operator
        builder.advanceLexer(true);

        PsiBuilder.Marker varMarker = builder.mark();

        IElementType next = ParserUtil.getTokenAndAdvance(builder, true);
        if (next == WORD) {
            varMarker.done(VAR_ELEMENT);

            IElementType endToken = ParserUtil.getTokenAndAdvance(builder);
            boolean validEnd = RIGHT_CURLY == endToken;

            if (validEnd) {
                marker.done(PARAM_EXPANSION_ELEMENT);
            } else {
                marker.drop();
            }

            return validEnd;
        }

        builder.error("Expected a variable");

        varMarker.drop();
        marker.drop();
        return false;
    }
}
