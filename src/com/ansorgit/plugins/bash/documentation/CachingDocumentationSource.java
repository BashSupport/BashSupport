/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: CachingDocumentationSource.java, Class: CachingDocumentationSource
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

/*
 */

package com.ansorgit.plugins.bash.documentation;

import com.google.common.collect.MapMaker;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * Caches the result of the delegate documentation source after the first invocation for a certain command name.
 * The cache is done in a weak key hash map to prevent too much memory allocation.
 * <p/>
 * The urls are not cached.
 * <p/>
 * User: jansorg
 * Date: 08.05.2010
 * Time: 12:59:53
 */
class CachingDocumentationSource implements DocumentationSource {
    private final CachableDocumentationSource delegate;

    //strong values to compare keys with equals(...)
    private final Map<String, String> documentationCache = new MapMaker().softValues().makeMap();

    public CachingDocumentationSource(CachableDocumentationSource source) {
        this.delegate = source;
    }

    @Nullable
    public String documentation(PsiElement element, PsiElement originalElement) {
        String key = delegate.findCacheKey(element, originalElement);
        if (key == null) {
            return delegate.documentation(element, originalElement);
        }

        if (!documentationCache.containsKey(key) || documentationCache.get(key) == null) {
            String data = delegate.documentation(element, originalElement);
            if (data == null) {
                return null;
            }

            documentationCache.put(key, data);
        }

        return documentationCache.get(key);
    }

    @Nullable
    public String documentationUrl(PsiElement element, PsiElement originalElement) {
        return delegate.documentationUrl(element, originalElement);
    }
}
