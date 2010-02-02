/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashInterpreterDetection.java, Class: BashInterpreterDetection
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

package com.ansorgit.plugins.bash.util;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

/**
 * Helper class to detect if there's is a Bash installation in one of the most common places.
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 12:48:42 PM
 */
public class BashInterpreterDetection {
    private static final List<String> guessLocations = Lists.newArrayList("/bin/bash", "/usr/bin/bash",
            "/usr/local/bin/bash", "/bin/sh", "/usr/bin/sh");

    public BashInterpreterDetection() {
    }

    public String findBestLocation() {
        for (String guessLocation : guessLocations) {
            File f = new File(guessLocation);

            if (f.isFile() && f.canRead()) {
                return guessLocation;
            }
        }

        return "";
    }
}
