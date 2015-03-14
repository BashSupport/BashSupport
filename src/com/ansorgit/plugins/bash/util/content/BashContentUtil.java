/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashContentUtil.java, Class: BashContentUtil
 * Last modified: 2010-02-28 19:53
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

import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;

import java.io.File;
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
            new ShebangHeuristic(1.0d),
            new EmptyFileHeuristic(0.4d),
            new PermissionHeuristic(0.4d));


    public static boolean isProbablyBashFile(@NotNull File file, double minProbabiliy) {
        return computeBashProbability(file, minProbabiliy) >= minProbabiliy;
    }

    public static double computeBashProbability(@NotNull File file, double minProbabiliy) {
        if (!file.isFile() || !file.canRead()) {
            return 0;
        }

        double result = 0;

        for (int i = 0, heuristicsSize = heuristics.size(); i < heuristicsSize && result < minProbabiliy; i++) {
            ContentHeuristic c = heuristics.get(i);
            result += c.isBashFile(file);
        }

        //Make sure the range is [0,1]
        return Math.min(1, Math.max(0, result));
    }
}
