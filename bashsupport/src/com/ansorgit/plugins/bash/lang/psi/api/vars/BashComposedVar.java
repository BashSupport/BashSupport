/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashComposedVar.java, Class: BashComposedVar
 * Last modified: 2010-12-09 19:40
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
 * A composed variable is a pattern in curly brackets, e.g. {ABC} . Usually a composed variable is
 * used as variable value, e.g. as ${ABC}.
 * It can contain a parameter expansion element.
 * <p/>
 * User: jansorg
 * Date: Oct 29, 2009
 * Time: 8:52:47 PM
 */
public interface BashComposedVar extends BashPsiElement {
}
