/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParsingChain.java, Class: ParsingChain
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

package com.ansorgit.plugins.bash.lang.parser;

import com.intellij.psi.tree.IElementType;

import java.util.LinkedList;
import java.util.List;

/**
 * A parsing chain contains a list of parsing functions which are all called after each other.
 * Basically this is a delegating ParsingFunction  which delegates to the first
 * parsing function which understands the token sequence. If no such function can be found
 * false is returned.
 * <p/>
 * Date: 02.05.2009
 * Time: 10:08:19
 *
 * @author Joachim Ansorg
 */
public abstract class ParsingChain implements ParsingFunction {
    private final List<ParsingFunction> parsingFunctions = new LinkedList<ParsingFunction>();

    protected void addParsingFunction(ParsingFunction f) {
        parsingFunctions.add(f);
    }

    public boolean isValid(BashPsiBuilder builder) {
        if (builder.eof()) return false;

        for (ParsingFunction f : parsingFunctions) {
            if (f.isValid(builder))
                return true;
        }

        return false;
    }

    public boolean isValid(IElementType token) {
        if (token == null) return false;

        for (ParsingFunction f : parsingFunctions) {
            if (f.isValid(token))
                return true;
        }

        return false;
    }

    public boolean parse(BashPsiBuilder builder) {
        if (builder.eof()) return false;

        for (ParsingFunction f : parsingFunctions) {
            if (f.isValid(builder))
                return f.parse(builder);
        }

        return false;
    }
}
