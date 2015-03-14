/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: PermissionHeuristic.java, Class: PermissionHeuristic
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

import com.intellij.openapi.project.Project;

import java.io.File;

/**
 * Checks whether a given file is executable. If it is then a positive score is returned.
 * <p/>
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 11:26:53 AM
 */
class PermissionHeuristic implements ContentHeuristic {
    private final double weight;

    public PermissionHeuristic(double weight) {
        this.weight = weight;
    }

    public double isBashFile(File file, String data, Project project) {
        if (file.canExecute()) {
            return weight;
        }

        return 0d;
    }
}
