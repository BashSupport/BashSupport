/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPsiElementImpl.java, Class: BashPsiElementImpl
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.psi.search.LocalSearchScope;
import com.intellij.psi.search.SearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * Date: 11.04.2009
 * Time: 23:29:38
 *
 * @author Joachim Ansorg
 */
public abstract class BashPsiElementImpl extends ASTWrapperPsiElement implements BashPsiElement {
    private final String name;

    public BashPsiElementImpl(final ASTNode astNode) {
        super(astNode);
        name = null;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return BashFileType.BASH_LANGUAGE;
    }

    public BashPsiElementImpl(final ASTNode astNode, final String name) {
        super(astNode);
        this.name = name;
    }

    @Override
    public String toString() {
        return name == null ? super.toString() : name;
    }

    @Override
    public SearchScope getUseScope() {
        return new LocalSearchScope(getContainingFile());
    }
}
