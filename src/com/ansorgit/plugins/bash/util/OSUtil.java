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

package com.ansorgit.plugins.bash.util;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions to handle differences of the current operating system. Mostly related to PATH environment and cygwin
 * file path handling.
 *
 * @author jansorg
 */
public final class OSUtil {
    private static final String CYGWIN_PREFIX = "/cygdrive/";

    private OSUtil() {
    }

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

    @Nullable
    public static String findBestExecutable(@NotNull String commandName) {
        String[] pathElements = StringUtils.split(System.getenv("PATH"), File.pathSeparatorChar);
        if (pathElements == null) {
            return null;
        }

        return findBestExecutable(commandName, Arrays.asList(pathElements));
    }

    @Nullable
    public static String findBestExecutable(@NotNull String commandName, @NotNull List<String> paths) {
        for (String path : paths) {
            String executablePath = findExecutable(commandName, path);
            if (executablePath != null) {
                return executablePath;
            }
        }

        return null;
    }

    @Nullable
    public static String findExecutable(@NotNull String commandName, String path) {
        String fullPath = path + File.separatorChar + commandName;
        File command = new File(fullPath);
        return command.exists() ? command.getAbsolutePath() : null;
    }
}
