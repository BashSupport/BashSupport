/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLineErrorFilter.java, Class: BashLineErrorFilter
 * Last modified: 2010-06-05
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

package com.ansorgit.plugins.bash.runner;

import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.RegexpFilter;
import com.intellij.openapi.project.Project;

/**
 * This is a custom line filter to insert hyperlinks to the line which has a problem.
 * Bash reports the errors in this format:
 * <p/>
 * User: jansorg
 * Date: Oct 31, 2009
 * Time: 10:36:04 PM
 */
public class BashLineErrorFilter extends RegexpFilter implements Filter {
    //e.g. /home/user/test.sh: line 13: notHere: command not found
    private static final String FILTER_REGEXP =
            RegexpFilter.FILE_PATH_MACROS + ": [a-zA-Z]+ " + RegexpFilter.LINE_MACROS + ": .+";

    public BashLineErrorFilter(Project project) {
        //: line (\d+):
        super(project, FILTER_REGEXP);
    }
}
