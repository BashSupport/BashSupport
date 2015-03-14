/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: LexerHeuristic.java, Class: LexerHeuristic
 * Last modified: 2010-02-22
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

package com.ansorgit.plugins.bash.util.content;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.lexer.BashTokenTypes;
import com.google.common.collect.Sets;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;

import java.io.File;
import java.util.Set;

/**
 * Lexes the file and evaluates the characteristics of the lexing process.
 * This operation is quite expensive in time and memory. If possible it should be executed very seldomly.
 * <p/>
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 5:32:41 PM
 */
class LexerHeuristic implements ContentHeuristic {
    private final double badCharacterWeight;
    private final double tokenLimitWeight;
    private final double tokenWeight;
    private final double modeWeight;

    public LexerHeuristic(double tokenWeight, double modeWeight) {
        badCharacterWeight = 0.3;
        tokenLimitWeight = 0.1;

        this.tokenWeight = tokenWeight;
        this.modeWeight = modeWeight;
    }

    public double isBashFile(File file, String data, Project project) {
        ParserDefinition definition = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);

        Lexer lexer = definition.createLexer(project);
        lexer.start(data);

        int tokenCount = 0;
        Set<IElementType> tokenSet = Sets.newHashSet();
        Set<Integer> modeSet = Sets.newHashSet();
        while (lexer.getTokenType() != BashTokenTypes.BAD_CHARACTER && lexer.getTokenType() != null) {
            tokenSet.add(lexer.getTokenType());
            modeSet.add(lexer.getState());

            lexer.advance();
            tokenCount++;
        }

        double score = 0;
        if (lexer.getTokenType() == BashTokenTypes.BAD_CHARACTER) {
            score -= badCharacterWeight;
        }

        if (tokenCount > 4) {
            score += tokenLimitWeight;
        }


        score += Math.min(0.45, (double) tokenSet.size() * tokenWeight);
        score += Math.min(0.45, (double) modeSet.size() * modeWeight);

        return score;
    }
}
