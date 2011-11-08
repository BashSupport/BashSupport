/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFunctions.java, Class: BashFunctions
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

package com.ansorgit.plugins.bash.util;

import com.google.common.base.Function;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;

/**
 * User: jansorg
 * Date: 06.02.11
 * Time: 21:01
 */
public class BashFunctions {
    public static Function<? super PsiFile, VirtualFile> psiToVirtualFile() {
        return new Function<PsiFile, VirtualFile>() {
            public VirtualFile apply(PsiFile psiFile) {
                return psiFile.getVirtualFile();
            }
        };
    }
}
