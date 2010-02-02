/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashPipelineImpl.java, Class: BashPipelineImpl
 * Last modified: 2009-12-04
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

package com.ansorgit.plugins.bash.lang.psi.impl.command;

import com.ansorgit.plugins.bash.lang.psi.api.command.BashPipeline;
import com.ansorgit.plugins.bash.lang.psi.impl.BashDelegatingElementImpl;
import com.intellij.lang.ASTNode;

/**
 * User: jansorg
 * Date: Dec 3, 2009
 * Time: 10:46:40 AM
 */
public class BashPipelineImpl extends BashDelegatingElementImpl implements BashPipeline {
    public BashPipelineImpl(final ASTNode astNode) {
        super(astNode, "pipeline command");
    }

}
