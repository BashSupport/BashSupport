/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: SystemPathUtil.java, Class: SystemPathUtil
 * Last modified: 2010-05-08 13:34
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

package com.ansorgit.plugins.bash.util;

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * User: jansorg
 * Date: 08.05.2010
 * Time: 11:22:08
 */
public class SystemPathUtil {
    private SystemPathUtil() {
    }

    @Nullable
    public static String findBestExecutable(@NotNull String commandName) {
        List<String> paths = Arrays.asList(StringUtils.split(System.getenv("PATH"), File.pathSeparatorChar));

        return findBestExecutable(commandName, paths);
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
