/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: SelectParsingFunction.java, Class: SelectParsingFunction
 * Last modified: 2010-02-09
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

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;
import com.ansorgit.plugins.bash.lang.parser.DefaultParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.Parsing;
import com.ansorgit.plugins.bash.lang.parser.util.ParserUtil;
import com.intellij.lang.PsiBuilder;

/**
 * Date: 02.05.2009
 * Time: 11:20:35
 *
 * @author Joachim Ansorg
 */
public class SelectParsingFunction extends DefaultParsingFunction {
    private final LoopParserUtil helper = new LoopParserUtil();

    public boolean isValid(BashPsiBuilder builder) {
        return builder.getTokenType() == SELECT_KEYWORD;
    }

    public boolean parse(BashPsiBuilder builder) {
        /*
        select_command:
                SELECT WORD newline_list DO list DONE
            |	SELECT WORD newline_list '{' list '}'
            |	SELECT WORD ';' newline_list DO list DONE
            |	SELECT WORD ';' newline_list '{' list '}'
            |	SELECT WORD newline_list IN word_list list_terminator newline_list DO list DONE
            |	SELECT WORD newline_list IN word_list list_terminator newline_list '{' list '}'
            ;
         */

        //log.assertTrue(isSelectCommand(builder.getTokenType()));
        final PsiBuilder.Marker selectCommand = builder.mark();
        builder.advanceLexer();//after the select

        if (ParserUtil.isIdentifier(builder.getTokenType())) {
            ParserUtil.markTokenAndAdvance(builder, VAR_DEF_ELEMENT);
        } else {
            ParserUtil.error(selectCommand, "parser.unexpected.token");
            return false;
        }

        builder.eatOptionalNewlines();

        if (builder.getTokenType() == IN_KEYWORD) {
            builder.advanceLexer();//after the IN
            boolean parsed = Parsing.word.parseWordList(builder, true, false); //include the terminator
            if (!parsed) {
                selectCommand.drop();
                return false;
            }
        }

        builder.eatOptionalNewlines();

        //now parse the body
        if (!helper.parseLoopBody(builder, false)) {
            selectCommand.drop();
            return false;
        }

        selectCommand.done(SELECT_COMMAND);
        return true;

    }
}
