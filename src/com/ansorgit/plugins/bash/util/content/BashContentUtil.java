/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashContentUtil.java, Class: BashContentUtil
 * Last modified: 2010-02-22
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

package com.ansorgit.plugins.bash.util.content;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * Utility class to work with bash content.
 * <p/>
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 11:20:17 AM
 */
public class BashContentUtil {
    private BashContentUtil() {
    }

    private static final List<? extends ContentHeuristic> heuristics = Lists.newArrayList(
            new ShebangHeuristic(1.0),
            new EmptyFileHeuristic(1.0d),
            new PermissionHeuristic(0.4d),
            new LexerHeuristic(0.1d, 0.15d));


    public static boolean isProbablyBashFile(@NotNull File file, double minProbabiliy, Project project) {
        return computeBashProbability(file, minProbabiliy, project) >= minProbabiliy;
    }

    public static double computeBashProbability(@NotNull File file, double minProbabiliy, Project project) {
        if (!file.isFile() || !file.canRead()) {
            return 0;
        }

        double result = 0;

        try {
            String data = FileUtil.loadTextAndClose(new FileReader(file));

            //fixme early return
            for (int i = 0, heuristicsSize = heuristics.size(); i < heuristicsSize && result < minProbabiliy; i++) {
                ContentHeuristic c = heuristics.get(i);
                result += c.isBashFile(file, data, project);
            }

            //Make sure the range is [0,1]
            return Math.min(1, Math.max(0, result));
        } catch (IOException e) {
            return 0;
        }
    }
}
