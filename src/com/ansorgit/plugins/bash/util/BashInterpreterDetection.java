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

import com.google.common.collect.Lists;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.util.SystemInfoRt;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Helper class to detect if there's is a Bash installation in one of the most common places.
 */
public class BashInterpreterDetection {
    public static final List<String> POSSIBLE_LOCATIONS = Collections.unmodifiableList(Lists.newArrayList(
            "/sbin/bash",
            "/bin/bash",
            "/usr/bin/bash",
            "/usr/local/bin/bash",
            "/opt/local/bin/bash",
            "/opt/bin/bash",
            "/sbin/sh",
            "/bin/sh",
            "/usr/bin/sh",
            "/opt/local/bin/sh",
            "/opt/bin/sh",
            "/usr/bin/env bash",
            "/usr/bin/env sh"
    ));

    private static final List<String> POSSIBLE_EXE_LOCATIONS = Collections.unmodifiableList(Lists.newArrayList(
            "/sbin/bash",
            "/bin/bash",
            "/usr/bin/bash",
            "/usr/local/bin/bash",
            "/opt/local/bin/bash",
            "/opt/bin/bash",
            "/sbin/sh",
            "/bin/sh",
            "/usr/bin/sh",
            "/opt/local/bin/sh",
            "/opt/bin/sh",
            "/usr/bin/env"
    ));

    private static final List<String> POSSIBLE_EXE_LOCATIONS_WINDOWS = Collections.unmodifiableList(Lists.newArrayList(
            "c:\\cygwin\\bin\\bash.exe", "d:\\cygwin\\bin\\bash.exe"
    ));

    public static final BashInterpreterDetection INSTANCE = new BashInterpreterDetection();

    public static BashInterpreterDetection instance() {
        return INSTANCE;
    }

    @Nullable
    public String findBestLocation() {
        List<String> locations = SystemInfo.isWindows ? POSSIBLE_EXE_LOCATIONS_WINDOWS : POSSIBLE_EXE_LOCATIONS;

        for (String guessLocation : locations) {
            if (isSuitable(guessLocation)) {
                return guessLocation;
            }
        }

        String pathLocation = OSUtil.findBestExecutable(SystemInfoRt.isWindows ? "bash.exe" : "bash");
        return isSuitable(pathLocation) ? pathLocation : null;
    }

    public boolean isSuitable(String guessLocation) {
        if (guessLocation == null) {
            return false;
        }

        File f = new File(guessLocation);
        return f.isFile() && f.canRead() && f.canExecute();
    }
}
