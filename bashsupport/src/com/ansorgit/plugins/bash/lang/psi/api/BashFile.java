/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFile.java, Class: BashFile
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

package com.ansorgit.plugins.bash.lang.psi.api;

import com.ansorgit.plugins.bash.lang.psi.api.function.BashFunctionDef;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.PsiFileWithStubSupport;

import java.util.Set;

/**
 * Date: 11.04.2009
 * Time: 23:22:57
 *
 * @author Joachim Ansorg
 */
public interface BashFile extends PsiFile, BashPsiElement, PsiFileWithStubSupport {
    Key<Boolean> LANGUAGE_CONSOLE_MARKER = new Key<Boolean>("Language console marker");

    boolean hasShebangLine();

    /**
     * @return
     */
    BashFunctionDef[] functionDefinitions();
}
