/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ClasspathDocSource.java, Class: ClasspathDocSource
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

package com.ansorgit.plugins.bash.documentation;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashCommand;
import com.intellij.psi.PsiElement;

/**
 * Base class for documentation source implementations
 * which load data from the classpath.
 * <p/>
 * Date: 03.05.2009
 * Time: 22:28:17
 *
 * @author Joachim Ansorg
 */
abstract class ClasspathDocSource implements DocumentationSource {
    private final String prefixPath;

    protected ClasspathDocSource(String prefixPath) {
        this.prefixPath = prefixPath;
    }

    public String documentation(PsiElement element, PsiElement originalElement) {
        if (!isValid(element, originalElement)) return null;

        BashCommand cmd = (BashCommand) element;
        return DocumentationReader.readFromFile(prefixPath, cmd.getReferencedName());
    }

    abstract boolean isValid(PsiElement element, PsiElement originalElement);
}
