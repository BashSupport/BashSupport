/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: GroupCommandParsingFunction.java, Class: GroupCommandParsingFunction
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

package com.ansorgit.plugins.bash.lang.parser.shellCommand;

import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.ansorgit.plugins.bash.lang.parser.BashElementTypes;
import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Date: 02.05.2009
 * Time: 11:20:45
 *
 * @author Joachim Ansorg
 */
public class GroupCommandParsingFunction extends DefaultParsingFunction {
    private static final Logger log = Logger.getInstance("#bash.GroupCommandParsingFunction");

    public boolean isValid(IElementType token) {
        return token == BashTokenTypes.LEFT_CURLY;
    }

    public boolean parse(BashPsiBuilder builder) {
        log.assertTrue(isValid(builder.getTokenType()));

        final PsiBuilder.Marker group = builder.mark();
        builder.advanceLexer();//after the { token

        //has to be a whitespace
        if (!ParserUtil.isWhitespace(builder.getTokenType(true))) {
            ParserUtil.error(group, "parser.unexpected.token");
            return false;
        }

        if (!Parsing.list.parseCompoundList(builder, true, false, false)) {
            ParserUtil.error(group, "parser.unexpected.token");//fixme
            return false;
        }

        //check the closing curly bracket
        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != BashTokenTypes.RIGHT_CURLY) {
            ParserUtil.error(group, "parser.unexpected.token");
            return false;
        }

        group.done(BashElementTypes.GROUP_COMMAND);
        return true;
    }
}
