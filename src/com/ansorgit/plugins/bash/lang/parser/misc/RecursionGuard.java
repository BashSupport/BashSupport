package com.ansorgit.plugins.bash.lang.parser.misc;

import com.ansorgit.plugins.bash.lang.parser.BashPsiBuilder;

/**
 * Logs recursive calls and is used to prevent accidental endless recursions.
 * https://code.google.com/p/bashsupport/issues/detail?id=152 is a report of an appareantly endless recursion which could
 * not be reproduced.
 */
public final class RecursionGuard {
    //the highest level found in the test files was 76 (as of 2015-02-28)
    private static final int MAX_NESTING = 150;

    private int max;
    private int level = 0;

    private RecursionGuard(int max) {
        this.max = max;
    }

    public static RecursionGuard initial() {
        return new RecursionGuard(MAX_NESTING);
    }

    public boolean next(BashPsiBuilder builder) {
        if (this.level++ > max) {
            builder.error("Internal parser error: Maximum level of nested calls reached. Please report this.");
            return false;
        }

        return true;
    }
}
