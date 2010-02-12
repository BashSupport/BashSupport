/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashFacetSettings.java, Class: BashFacetSettings
 * Last modified: 2010-02-12
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

package com.ansorgit.plugins.bash.settings.facet;

/**
 * User: jansorg
 * Date: Feb 12, 2010
 * Time: 9:32:30 PM
 */
public class BashFacetSettings {
    public static enum Mode {
        IgnoreAll, AcceptAll, CustomSettings
    }

    public Mode mode = Mode.IgnoreAll;
}
