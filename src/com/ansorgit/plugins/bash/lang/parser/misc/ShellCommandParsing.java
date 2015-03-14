/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ShellCommandParsing.java, Class: ShellCommandParsing
 * Last modified: 2010-05-11
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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.ParsingChain;
import com.ansorgit.plugins.bash.lang.parser.ParsingFunction;
import com.ansorgit.plugins.bash.lang.parser.ParsingTool;
import com.ansorgit.plugins.bash.lang.parser.shellCommand.*;

/**
 * Date: 25.03.2009
 * Time: 11:17:35
 *
 * @author Joachim Ansorg
 */
public class ShellCommandParsing extends ParsingChain implements ParsingTool {
    public ShellCommandParsing() {
        /*
       shell_command 	: for_command
           | case_command
           | WHILE compound_list DO compound_list DONE
           | UNTIL compound_list DO compound_list DONE
           | select_command
           | if_command
           | subshell
           | group_command
           | arith_command
           | cond_command
           | arith_for_command
           ;
        */
        addParsingFunction(forLoopParser);
        addParsingFunction(caseParser);
        addParsingFunction(whileParser);
        addParsingFunction(untilParser);
        addParsingFunction(selectParser);
        addParsingFunction(ifParser);
        addParsingFunction(subshellParser);
        addParsingFunction(groupCommandParser);
        addParsingFunction(arithmeticParser);
        addParsingFunction(conditionalParser);
    }

    public final CaseParsingFunction caseParser = new CaseParsingFunction();

    public final ParsingFunction whileParser = new WhileLoopParserFunction();

    public final ParsingFunction untilParser = new UntilLoopParserFunction();

    public final ParsingFunction selectParser = new SelectParsingFunction();

    public final ArithmeticParser arithmeticParser = new ArithmeticParser();

    public final ParsingFunction conditionalParser = new ConditionalParsingFunction();

    public final ParsingFunction ifParser = new IfParsingFunction();

    public final ParsingFunction forLoopParser = new ForLoopParsingFunction();

    public final ParsingFunction subshellParser = new SubshellParsingFunction();

    public final ParsingFunction groupCommandParser = new GroupCommandParsingFunction();

    public final ParsingFunction backtickParser = new BacktickParsingFunction();
}
