/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: CachableDocumentationSource.java, Class: CachableDocumentationSource
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

import com.intellij.psi.PsiElement;

/**
 * Extends a documentation source with a cache key providing method. A CachableDocumentationSource
 * takes a CachableDocumentationSource and used the key to store the data with its value in the cache.
 * <p/>
 * User: jansorg
 * Date: 08.05.2010
 * Time: 13:13:17
 */
interface CachableDocumentationSource extends DocumentationSource {
    String findCacheKey(PsiElement element, PsiElement originalElement);
}
