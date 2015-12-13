package com.ansorgit.plugins.bash.lang.psi.stubs.index;

/**
 * Configures the versions of the available Bash indexes.
 */
public final class BashIndexVersion {
    private static final int BASE = 7;
    public static final int CACHES_VERSION = BASE + 9;
    public static final int STUB_INDEX_VERSION = BASE + 31;
    public static final int ID_INDEX_VERSION = BASE + 19;

    private BashIndexVersion() {
    }
}
