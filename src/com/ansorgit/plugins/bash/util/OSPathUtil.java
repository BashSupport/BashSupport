package com.ansorgit.plugins.bash.util;

import org.apache.commons.lang.StringUtils;

import java.io.File;

/**
 *
 */
public class OSPathUtil {
    private static final String CYGWIN_PREFIX = "/cygdrive/";

    public static String toBashCompatible(String path) {
        path = StringUtils.replace(path, File.separator, "/");
        if (path.length() > 3 && path.substring(1, 3).equals(":/")) {
            path = CYGWIN_PREFIX + path.substring(0, 1) + path.substring(2);
        }

        return path;
    }

    public static String bashCompatibleToNative(String cygwinPath) {
        if (cygwinPath.startsWith(CYGWIN_PREFIX) && cygwinPath.length() > CYGWIN_PREFIX.length() + 2) {
            String driveLetter = cygwinPath.substring(CYGWIN_PREFIX.length(), CYGWIN_PREFIX.length() + 1);

            return driveLetter + ":" + File.separator + StringUtils.replace(cygwinPath.substring("/cygwin/".length() + 4), "/", File.separator);
        }

        return cygwinPath;
    }
}
