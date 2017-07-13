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

package com.ansorgit.plugins.bash.lang.psi.impl.vars;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashAssignmentList;
import com.ansorgit.plugins.bash.lang.psi.impl.BashBaseElement;
import com.intellij.lang.ASTNode;

/**
 * @author jansorg
 */
public class BashAssignmentListImpl extends BashBaseElement implements BashAssignmentList {
    public BashAssignmentListImpl(ASTNode node) {
        super(node, "assignment list");
    }
}
