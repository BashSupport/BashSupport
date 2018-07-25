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

package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.jetbrains.ExtendedRegexFilter;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;

/**
 * This is a custom line filter to insert hyperlinks to the line which has a problem.
 * Bash reports the errors in this format:
 *      /home/user/test.sh: line 13: notHere: command not found
 */
public class BashLineErrorFilter extends ExtendedRegexFilter implements Filter {
    //e.g. /home/user/test.sh: line 13: notHere: command not found
    private static final String FILTER_REGEXP = ExtendedRegexFilter.FILE_PATH_MACROS
            + ": [a-zA-Z]+ "
            + ExtendedRegexFilter.LINE_MACROS + ": .+";

    public BashLineErrorFilter(Project project) {
        //: line (\d+):
        super(project, FILTER_REGEXP);
    }
}
