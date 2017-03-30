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
 * <br>
 * @author jansorg
 */
public class GroupCommandParsingFunction implements ParsingFunction {
    public boolean isValid(BashPsiBuilder builder) {
        return builder.rawLookup(0) == LEFT_CURLY && ParserUtil.isWhitespaceOrLineFeed(builder.rawLookup(1));
    }

    public boolean parse(BashPsiBuilder builder) {
        final PsiBuilder.Marker group = builder.mark();
        builder.advanceLexer(); //the { token
        if (builder.rawLookup(0) == LINE_FEED) { // whitespace doesn't have to be read here
            builder.advanceLexer();
        }

        Parsing.list.parseCompoundList(builder, true, false);

        //check the closing curly bracket
        final IElementType lastToken = ParserUtil.getTokenAndAdvance(builder);
        if (lastToken != BashTokenTypes.RIGHT_CURLY) {
            group.drop();
            return false;
        }

        group.done(BashElementTypes.GROUP_COMMAND);
        return true;
    }
}
