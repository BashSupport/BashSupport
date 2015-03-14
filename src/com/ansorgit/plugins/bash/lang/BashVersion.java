/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashVersion.java, Class: BashVersion
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

package com.ansorgit.plugins.bash.lang;

/**
 * Enumeration of the supported Bash versions.
 * The plugin has a Bash v4 mode and a Bash v3 mode. The parser and lexer
 * can be configured to also support the v4 features / changes.
 * <p/>
 * User: jansorg
 * Date: Dec 1, 2009
 * Time: 7:47:20 PM
 */
public enum BashVersion {
    Bash_v3, Bash_v4
}
