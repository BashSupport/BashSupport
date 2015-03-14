/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarDefContainer.java, Class: BashVarDefContainer
 * Last modified: 2010-02-06 10:50
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

package com.ansorgit.plugins.bash.lang.psi.api.vars;

import com.ansorgit.plugins.bash.lang.psi.api.BashPsiElement;

/**
 * Marker interface for psi elements which may contain child variable definitions which are visible outside of
 * the element itself.
 * <p/>
 * User: jansorg
 * Date: 29.07.2009
 * Time: 20:36:15
 */
public interface BashVarDefContainer extends BashPsiElement {
}
