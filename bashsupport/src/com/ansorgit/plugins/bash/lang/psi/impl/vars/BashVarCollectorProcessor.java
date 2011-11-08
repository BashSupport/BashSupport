/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVarCollectorProcessor.java, Class: BashVarCollectorProcessor
 * Last modified: 2011-02-07 19:57
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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.intellij.psi.scope.PsiScopeProcessor;

import java.util.List;

/**
 * User: jansorg
 * Date: 07.02.11
 * Time: 19:55
 */
public interface BashVarCollectorProcessor extends PsiScopeProcessor {
    List<BashVarDef> getVariables();
}
