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

package com.ansorgit.plugins.bash.lang.psi.api;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BashFileReference extends BashPsiElement {
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

    /**
     * @return {@code true} if the value contains dynamic values, e.g. variables.
     */
    default boolean isDynamic(){
        return !isStatic();
    }
}