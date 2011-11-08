/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BackquoteData.java, Class: BackquoteData
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

package com.ansorgit.plugins.bash.lang.parser;

/**
 * User: jansorg
 * Date: Jan 29, 2010
 * Time: 7:12:52 PM
 */
public final class BackquoteData {
    private boolean inBackquote = false;

    public void enterBackquote() {
        this.inBackquote = true;
    }

    public void leaveBackquote() {
        this.inBackquote = false;
    }

    public boolean isInBackquote() {
        return inBackquote;
    }
}
