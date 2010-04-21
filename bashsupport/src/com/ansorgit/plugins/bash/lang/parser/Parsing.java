/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: Parsing.java, Class: Parsing
 * Last modified: 2010-04-21
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

package com.ansorgit.plugins.bash.lang.parser;

import com.ansorgit.plugins.bash.lang.parser.command.CommandParsing;
import com.ansorgit.plugins.bash.lang.parser.command.PipelineParsing;
import com.ansorgit.plugins.bash.lang.parser.misc.*;
import com.ansorgit.plugins.bash.lang.parser.variable.VarParsing;

/**
 * The registry for all parsing related helper classes.
 * It gives access to the available parsing helpers. The instances are only created
 * one.
 * <p/>
 * Date: 24.03.2009
 * Time: 21:34:44
 *
 * @author Joachim Ansorg
 */
public final class Parsing {
    public static final FileParsing file = new FileParsing();
    public static final RedirectionParsing redirection = new RedirectionParsing();
    public static final CommandParsing command = new CommandParsing();
    public static final ShellCommandParsing shellCommand = new ShellCommandParsing();
    public static final ListParsing list = new ListParsing();
    public static final PipelineParsing pipeline = new PipelineParsing();
    public static final WordParsing word = new WordParsing();
    public static final VarParsing var = new VarParsing();
    public static final HereDocParsing hereDoc = new HereDocParsing();
    public static final BraceExpansionParsing braceExpansionParsing = new BraceExpansionParsing();
    public static final ParameterExpansionParsing parameterExpansionParsing = new ParameterExpansionParsing();

    private Parsing() {
    }
}
