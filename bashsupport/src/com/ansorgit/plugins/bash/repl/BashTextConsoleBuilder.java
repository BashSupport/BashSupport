/*
 * Copyright 2009 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTextConsoleBuilder.java, Class: BashTextConsoleBuilder
 * Last modified: 2010-03-03
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

package com.ansorgit.plugins.bash.repl;

import com.ansorgit.plugins.bash.file.BashFileType;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: jansorg
 * Date: Mar 3, 2010
 * Time: 10:41:12 PM
 * To change this template use File | Settings | File Templates.
 */
class BashTextConsoleBuilder extends TextConsoleBuilderImpl {
    private final ArrayList<Filter> filters = new ArrayList<Filter>();
    private final Project project;

    public BashTextConsoleBuilder(Project project) {
        super(project);
        this.project = project;
    }

    @Override
    public ConsoleView getConsole() {
        final ConsoleViewImpl view = new ConsoleViewImpl(project, true, BashFileType.BASH_FILE_TYPE);
        for (Filter filter : filters) {
            view.addMessageFilter(filter);
        }

        return view;
    }

    @Override
    public void addFilter(Filter filter) {
        filters.add(filter);
    }
}
