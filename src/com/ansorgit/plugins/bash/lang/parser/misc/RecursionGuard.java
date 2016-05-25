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

package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;

/**
 * Logs recursive calls and is used to prevent accidental endless recursions.
 * https://code.google.com/p/bashsupport/issues/detail?id=152 is a report of an appareantly endless recursion which could
 * not be reproduced.
 */
final class RecursionGuard {
    private final int max;
    private int level = 0;

    private RecursionGuard(int max) {
        this.max = max;
    }

    //the highest level found in the test files was 76 (as of 2015-02-28)
    //https://github.com/jansorg/BashSupport/issues/310 needs more than 150 levels
    public static RecursionGuard initial() {
        return initial(1000);
    }

    public static RecursionGuard initial(int maxNestingLevel) {
        return new RecursionGuard(maxNestingLevel);
    }

    public boolean next(BashPsiBuilder builder) {
        if (++level > max) {
            builder.error("Internal parser error: Maximum level '" + max + "' of nested calls reached. Please report this at https://github.com/jansorg/BashSupport/issues");
            return false;
        }

        return true;
    }
}
