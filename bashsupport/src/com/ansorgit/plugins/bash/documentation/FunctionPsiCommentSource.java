/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: FunctionPsiCommentSource.java, Class: FunctionPsiCommentSource
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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.DocumentationAwareElement;
import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;

import java.util.List;

/**
 * Provides the comment right before a function definition as documentation for a function call psi element and
 * for the definition itself.
 * <p/>
 * User: jansorg
 * Date: Nov 10, 2009
 * Time: 7:12:28 PM
 */
class FunctionPsiCommentSource implements DocumentationSource {
    private static final Logger log = Logger.getInstance("#bash.FunctionPsiCommentSource");

    public String documentation(PsiElement element, PsiElement originalElement) {
        if (element instanceof DocumentationAwareElement) {
            log.debug("Looking for doc for doc aware psi element");
            return psiElementDocumentation((DocumentationAwareElement) element);
        }

        if (element instanceof BashCommand) {
            BashCommand command = (BashCommand) element;
            if (command.isFunctionCall()) {
                log.debug("Looking for doc for function call");

                PsiElement function = command.resolve();

                if (function instanceof DocumentationAwareElement) {
                    return psiElementDocumentation((DocumentationAwareElement) function);
                }
            }
        }

        return null;
    }

    private String psiElementDocumentation(DocumentationAwareElement element) {
        List<PsiComment> psiComments = element.findAttachedComment();
        return psiComments != null ? cleanupComment(joinComments(psiComments)) : null;
    }

    private String joinComments(List<PsiComment> psiComments) {
        StringBuilder command = new StringBuilder();

        for (PsiComment comment : psiComments) {
            command.append(comment.getText()).append("\n");
        }

        return command.toString();
    }

    private String cleanupComment(String text) {
        List<String> lines = StringUtil.split(text, "\n");

        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(StringUtil.trimStart(line.substring(1), " "));
            result.append("<br/>\n");
        }

        return result.toString();
    }

    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        return null;
    }
}
