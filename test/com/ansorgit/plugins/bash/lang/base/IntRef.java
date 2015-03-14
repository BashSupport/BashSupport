/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: IntRef.java, Class: IntRef
 * Last modified: 2010-01-19
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

package com.ansorgit.plugins.bash.lang.base;

import com.intellij.openapi.util.Ref;

public class IntRef extends Ref<Integer> {
    public IntRef(Integer value) {
        super(value);
    }

    public void inc(final int value) {
        set(get() + value);
    }

    public void dec(final int value) {
        set(get() - value);
    }
}
