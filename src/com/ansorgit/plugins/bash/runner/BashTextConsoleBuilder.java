/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: BashTextConsoleBuilder.java, Class: BashTextConsoleBuilder
 * Last modified: 2010-05-13
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
import com.intellij.execution.filters.TextConsoleBuilder;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.project.Project;

import java.util.ArrayList;

/**
 * @author dyoma
 */
public class BashTextConsoleBuilder extends TextConsoleBuilder {
    private final Project myProject;
    private final ArrayList<Filter> myFilters = new ArrayList<Filter>();
    private boolean myViewer;

    public BashTextConsoleBuilder(final Project project) {
        myProject = project;
    }

    public ConsoleView getConsole() {
        final ConsoleView consoleView = createConsole();
        for (final Filter filter : myFilters) {
            consoleView.addMessageFilter(filter);
        }

        return consoleView;
    }

    @Override
    public void addFilter(Filter filter) {
    }

    protected ConsoleViewImpl createConsole() {
        return new ConsoleViewImpl(myProject, myViewer);
    }

    @Override
    public void setViewer(boolean isViewer) {
        myViewer = isViewer;
    }
}
