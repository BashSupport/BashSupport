/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangHeuristic.java, Class: ShebangHeuristic
 * Last modified: 2011-09-03 14:30
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

package com.ansorgit.plugins.bash.util.content;

import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import com.intellij.openapi.util.io.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Reads the first bytes of the file.
 */
class ShebangHeuristic implements ContentHeuristic {
    private static final List<String> validStarts = Lists.newArrayList();
    private static int readLimit = 1;

    static {
        for (String location : BashInterpreterDetection.guessLocations) {
            validStarts.add("#!" + location);
            readLimit = Math.max(readLimit, location.length() + 2);
        }
    }

    private final double weight;

    public ShebangHeuristic(double weight) {
        this.weight = weight;
    }

    public double isBashFile(File file) {
        try {
            String data = FileUtil.loadTextAndClose(ByteStreams.limit(new FileInputStream(file), readLimit));

            for (String s : validStarts) {
                if (data.startsWith(s)) {
                    return weight;
                }
            }

            return 0;
        } catch (java.io.IOException e) {
            return 0;
        }
    }
}
