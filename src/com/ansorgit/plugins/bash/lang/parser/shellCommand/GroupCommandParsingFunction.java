/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: GroupCommandParsingFunction.java, Class: GroupCommandParsingFunction
 * Last modified: 2011-04-30 16:33
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;

/**
 * Parsing function for blocks / group commands.
 * <p/>
 * Date: 02.05.2009
 * Time: 11:20:45
 *
 * @author Joachim Ansorg
 */
public class GroupCommandParsingFunction implements ParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        PsiBuilder.Marker marker = builder.mark();

        boolean result = ParserUtil.conditionalRead(builder, LEFT_CURLY) && ParserUtil.isWhitespace(builder.getTokenType(true));

        marker.rollbackTo();
        return result;
    }

    public boolean parse(BashPsiBuilder builder) {
        final PsiBuilder.Marker group = builder.mark();
        builder.advanceLexer();//after the { token

        //has to be a whitespace
        if (!ParserUtil.isWhitespace(builder.getTokenType(true))) {
            ParserUtil.error(group, "parser.unexpected.token");
            return false;
        }

        if (!Parsing.list.parseCompoundList(builder, true, false)) {
            //ParserUtil.error(group, "parser.unexpected.token");
            //group.drop();
            //return false;
        }

        //check the closing curly bracket
        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != BashTokenTypes.RIGHT_CURLY) {
            //ParserUtil.error(group, "parser.unexpected.token");
            group.drop();
            return false;
        }

        group.done(BashElementTypes.GROUP_COMMAND);
        return true;
    }
}
