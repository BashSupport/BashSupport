/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashResolveStates.java, Class: BashResolveStates
 * Last modified: 2010-06-30
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

package com.ansorgit.plugins.bash.lang.psi.impl;

import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.KeyWithDefaultValue;

/**
 * User: jansorg
 * Date: 29.06.2010
 * Time: 19:53:50
 */
public class BashResolveStates {
    private BashResolveStates() {
    }

    public static final Key<Boolean> RESOLVE_SUCCESS = new KeyWithDefaultValue<Boolean>("RESOLVE_SUCCESS") {
        @Override
        public Boolean getDefaultValue() {
            return false;
        }
    };
}
