/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashDocumentationProvider.java, Class: BashDocumentationProvider
 * Last modified: 2010-10-16
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
import com.intellij.lang.documentation.QuickDocumentationProvider;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;

import java.util.List;

/**
 * This is the bash documentation provider. A documentation provider is called
 * for PsiElements which implement the interface PsiReference.
 * <p/>
 * Date: 12.04.2009
 * Time: 21:26:50
 *
 * @author Joachim Ansorg
 */
public class BashDocumentationProvider extends QuickDocumentationProvider {
    private static final Logger log = Logger.getInstance("#bash.BashDocumentationProvider");

    public String getQuickNavigateInfo(PsiElement psiElement) {
        log.info("getQuickNavigateInfo " + psiElement);

        return null;
    }

    public String getQuickNavigateInfo(PsiElement psiElement, PsiElement psiElement1) {
        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        log.info("getUrlFor " + element);
        return Lists.newArrayList(DocumentationProvider.documentationUrl(element, originalElement));
    }

    /**
     * Generates the documentation for a given PsiElement. The original
     * element is the token the caret was on at the time the documentation
     * was called.
     *
     * @param element         The element for which the documentation has been requested.
     * @param originalElement The element the caret is on
     * @return The HTML formatted String which contains the documentation.
     */
    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        log.info("generateDoc() for " + element + " and " + originalElement);

        return DocumentationProvider.documentation(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        log.info("getDocumentationElementForLookupItem: element: " + element);
        return element;
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        log.info("getDocumentationElementForLink: element: " + context);
        return context;
    }
}
