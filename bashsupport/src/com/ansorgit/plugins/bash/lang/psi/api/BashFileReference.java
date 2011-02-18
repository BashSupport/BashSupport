/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFile.java, Class: BashFile
 * Last modified: 2010-06-30
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

package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Date: 11.04.2009
 * Time: 23:22:57
 *
 * @author Joachim Ansorg
 */
public interface BashFileReference extends BashPsiElement, PsiReference {
    @Nullable
    PsiFile findReferencedFile();

    @NotNull
    String getFilename();

    /**
     * Returns if the reference consists of static text or if it is variable at runtime.
     *
     * @return True if it is static.
     */
    boolean isStatic();
}