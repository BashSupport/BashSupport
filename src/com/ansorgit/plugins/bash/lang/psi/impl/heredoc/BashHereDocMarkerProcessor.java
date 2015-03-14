/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BasHereDocMarkerProcessor.java, Class: BasHereDocMarkerProcessor
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

package com.ansorgit.plugins.bash.lang.psi.impl.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.ansorgit.plugins.bash.lang.psi.util.BashAbstractProcessor;
import com.intellij.openapi.util.Key;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;

/**
 * PSI tree processor which collects heredoc markers in the tree.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 12:55:10 PM
 */
class BashHereDocMarkerProcessor extends BashAbstractProcessor {
    private final String referencedName;
    private final Class<? extends BashPsiElement> otherEndsType;

    public BashHereDocMarkerProcessor(String referencedName, Class<? extends BashPsiElement> otherEndsType) {
        super(true);

        this.referencedName = referencedName;
        this.otherEndsType = otherEndsType;
    }

    public boolean execute(PsiElement element, ResolveState state) {
        boolean isValid = otherEndsType.isInstance(element) && referencedName.equals(element.getText());
        if (isValid) {
            storeResult(element, 100);
            return false;
        }

        return true;
    }

    public <T> T getHint(Key<T> hintKey) {
        return null;
    }
}
