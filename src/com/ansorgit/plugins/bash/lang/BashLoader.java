/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashLoader.java, Class: BashLoader
 * Last modified: 2010-05-10 20:17
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

package com.ansorgit.plugins.bash.lang;

import com.ansorgit.plugins.bash.BashComponents;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.ProjectManagerAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * The loader for the BashSupport plugin.
 * <p/>
 * Date: 22.03.2009
 * Time: 11:06:12
 *
 * @author Joachim Ansorg
 */
public class BashLoader implements ApplicationComponent {
    public static BashLoader getInstance() {
        return ApplicationManager.getApplication().getComponent(BashLoader.class);
    }

    @NotNull
    public String getComponentName() {
        return BashComponents.BASH_LOADER;
    }

    public void initComponent() {
        ProjectManager.getInstance().addProjectManagerListener(new ProjectManagerAdapter() {
            public void projectOpened(final Project project) {
            }
        });
    }

    public void disposeComponent() {
    }
}
