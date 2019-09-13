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

package com.ansorgit.plugins.bash.lang.psi.stubs.index;

/**
 * Configures the versions of the available Bash indexes.
 */
public final class BashIndexVersion {
    private static final int BASE = 63;
    public static final int CACHES_VERSION = BASE + 9;
    public static final int STUB_INDEX_VERSION = BASE + 31;
    public static final int ID_INDEX_VERSION = BASE + 19;

    private BashIndexVersion() {
    }
}