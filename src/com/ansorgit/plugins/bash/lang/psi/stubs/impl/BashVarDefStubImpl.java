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

package com.ansorgit.plugins.bash.lang.psi.stubs.impl;

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVarDef;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarDefStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author jansorg
 */
public class BashVarDefStubImpl extends StubBase<BashVarDef> implements BashVarDefStub {
    private final StringRef name;
    private boolean readOnly;

    public BashVarDefStubImpl(StubElement parent, StringRef name, final IStubElementType elementType, boolean readOnly) {
        super(parent, elementType);
        this.name = name;
        this.readOnly = readOnly;
    }

    public String getName() {
        return StringRef.toString(name);
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }
}