/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashHereDocMarker.java, Class: BashHereDocMarker
 * Last modified: 2010-02-06 10:50
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ansorgit.plugins.bash.lang.psi.api.heredoc;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;
import com.intellij.psi.PsiNamedElement;

/**
 * Marker interface for all heredoc marker psi elements.
 * <p/>
 * User: jansorg
 * Date: Jan 30, 2010
 * Time: 1:57:34 PM
 */
public interface BashHereDocMarker extends BashPsiElement, PsiNamedElement {
    String getMarkerText();
}
