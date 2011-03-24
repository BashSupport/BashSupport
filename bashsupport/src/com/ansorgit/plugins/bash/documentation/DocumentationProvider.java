/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: DocumentationProvider.java, Class: DocumentationProvider
 * Last modified: 2010-05-08
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

package com.ansorgit.plugins.bash.documentation;

import com.google.common.collect.Lists;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * This class encapsulates the documentation retrieval.
 * It contains a list of documentation sources. A source is responsible
 * for one kind of elements, e.g. built-in bash commands.
 * <p/>
 * Date: 03.05.2009
 * Time: 18:22:32
 *
 * @author Joachim Ansorg
 */
class DocumentationProvider {
    private static final Logger log = Logger.getInstance("#bash.DocumentationProvider");
    private static final List<DocumentationSource> sourceList = Lists.newArrayList();

    static {
        sourceList.add(new FunctionPsiCommentSource());
        sourceList.add(new BashKeywordDocSource());
        sourceList.add(new InternalCommandDocumentation());
        sourceList.add(new CachingDocumentationSource(new SystemInfopageDocSource()));
        sourceList.add(new ManpageDocSource());
    }

    /**
     * Returns the documentation for the given element and the originalElement.
     * It iterates through the list of documentation sources and returns the first
     * hit.
     *
     * @param element         The element for which the documentation is requested.
     * @param originalElement The element the caret was on.
     * @return The HTML formatted documentation string.
     */
    static String documentation(PsiElement element, PsiElement originalElement) {
        log.info("documentation for " + element);

        for (DocumentationSource source : sourceList) {
            log.info("Trying with " + source);

            String doc = source.documentation(element, originalElement);
            if (StringUtils.stripToNull(doc) != null) {
                return doc;
            }
        }

        return "No documentation found.";
    }

    /**
     * Returns an external link to a command.
     *
     * @param element         The element for which the documentation is requested.
     * @param originalElement The element the caret was on.
     * @return The url which leads to the online documentation.
     */
    static String documentationUrl(PsiElement element, PsiElement originalElement) {
        log.info("documentationUrl for " + element);
        for (DocumentationSource source : sourceList) {
            String url = source.documentationUrl(element, originalElement);
            if (StringUtils.stripToNull(url) != null) {
                return url;
            }
        }

        return null;
    }

    /**
     * This is a static-only class so we keep the constructor private.
     */
    private DocumentationProvider() {
    }
}
