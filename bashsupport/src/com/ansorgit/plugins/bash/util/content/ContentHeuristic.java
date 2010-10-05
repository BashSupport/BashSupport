/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: ContentHeuristic.java, Class: ContentHeuristic
 * Last modified: 2010-06-30
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
 * User: jansorg
 * Date: Feb 20, 2010
 * Time: 11:23:26 AM
 */
interface ContentHeuristic {
    /**
     * Returns a probability that the given file is a bash file.
     *
     * @param file    The file to check.
     * @param data
     * @param project
     * @return A value in the range of [0,1] which is the probability that the file is a Bash script or include file.
     *         0 means that it's surely not a Bash script. 1 means that it's definitely a Bash file.
     */
    double isBashFile(File file, String data, Project project);
}
