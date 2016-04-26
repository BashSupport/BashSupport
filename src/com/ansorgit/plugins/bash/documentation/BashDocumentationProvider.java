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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashGenericCommand;
import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.util.BashPsiUtils;
import com.google.common.collect.Lists;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.SystemInfoRt;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

/**
 * This is the bash documentation provider. A documentation provider is called
 * for PsiElements which implement the interface PsiReference.
 * <br>
 *
 * @author jansorg
 */
public class BashDocumentationProvider extends AbstractDocumentationProvider {
    private final List<DocumentationSource> sourceList;

    public BashDocumentationProvider() {
        sourceList = Lists.newArrayList();
        sourceList.add(new PsiElementCommentSource());
        sourceList.add(new BashKeywordDocSource());
        sourceList.add(new InternalCommandDocumentation());
        sourceList.add(new ManpageDocSource());

        if (!SystemInfoRt.isWindows) {
            //there is currently no support for the cygwin info command
            sourceList.add(new CachingDocumentationSource(new SystemInfopageDocSource()));
        }
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement instanceof BashVar) {
            return null;
        }

        return BashPsiUtils.findParent(contextElement, BashGenericCommand.class);
    }

    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        for (DocumentationSource source : sourceList) {
            String url = source.documentationUrl(element, originalElement);
            if (StringUtils.stripToNull(url) != null) {
                return Collections.singletonList(url);
            }
        }

        return null;
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
        for (DocumentationSource source : sourceList) {
            String doc = source.documentation(element, originalElement);
            if (StringUtils.stripToNull(doc) != null) {
                return doc;
            }
        }

        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        return element;
    }

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return context;
    }
}
