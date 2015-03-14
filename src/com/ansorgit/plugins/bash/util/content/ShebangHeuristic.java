/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: ShebangHeuristic.java, Class: ShebangHeuristic
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

import java.io.File;
import java.util.List;

/**
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 11:28:48 AM
 */
class ShebangHeuristic implements ContentHeuristic {
    private static final List<String> validStarts = Lists.newArrayList("#!/bin/sh", "#!/bin/bash",
            "#!/usr/bin/sh", "#!/usr/bin/bash");
    private final double weight;

    public ShebangHeuristic(double weight) {
        this.weight = weight;
    }

    public double isBashFile(File file, String data, Project project) {
        for (String s : validStarts) {
            if (data.startsWith(s)) {
                return weight;
            }
        }

        return 0;
    }
}
