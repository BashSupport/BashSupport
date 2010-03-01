/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ParserHeuristic.java, Class: ParserHeuristic
 * Last modified: 2010-03-01
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

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * Lexes the file and evaluates the characteristics of the lexing process.
 * This operation is quite expensive in time and memory. If possible it should be executed very seldomly.
 * <p/>
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 5:32:41 PM
 */
class ParserHeuristic implements ContentHeuristic {

    public ParserHeuristic() {
    }

    public double isBashFile(File file, String data, Project project) {
        /*ParserDefinition definition = LanguageParserDefinitions.INSTANCE.forLanguage(BashFileType.BASH_LANGUAGE);

        Lexer lexer = definition.createLexer(project);
        lexer.start(data);

        PsiParser parser = definition.createParser(project);
        ASTNode astNode = parser.parse(lexer.getTokenType(), new BashPsiBuilder(psiBuilder, BashVersion.Bash_v4));

        PsiElement[] errors = PsiTreeUtil.collectElements(astNode.getPsi(), new PsiElementFilter() {
            public boolean isAccepted(PsiElement element) {
                return element instanceof PsiErrorElement;
            }
        });

        return errors.length == 0 ? 1.0 : 1.0d / errors.length;*/
        return 0.0d;
    }
}