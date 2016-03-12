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

import com.ansorgit.plugins.bash.lang.psi.api.vars.BashVar;
import com.ansorgit.plugins.bash.lang.psi.stubs.api.BashVarStub;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.intellij.util.io.StringRef;

/**
 * @author jansorg
 */
public class BashVarStubImpl extends StubBase<BashVar> implements BashVarStub {
    private final StringRef name;
    private int prefixLength;

    public BashVarStubImpl(StubElement parent, StringRef name, final IStubElementType elementType, int prefixLength) {
        super(parent, elementType);
        this.name = name;
        this.prefixLength = prefixLength;
    }

    public String getName() {
        return StringRef.toString(name);
    }

    @Override
    public int getPrefixLength() {
        return prefixLength;
    }
}