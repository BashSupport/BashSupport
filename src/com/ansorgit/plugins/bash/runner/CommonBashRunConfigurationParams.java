/*
 * Copyright 2010 Joachim Ansorg, mail@ansorg-it.com
 * File: CommonBashRunConfigurationParams.java, Class: CommonBashRunConfigurationParams
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

package com.ansorgit.plugins.bash.runner;

import java.util.Map;

/**
 * This class uses code from the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public interface CommonBashRunConfigurationParams {
    String getInterpreterOptions();

    void setInterpreterOptions(String options);

    String getWorkingDirectory();

    void setWorkingDirectory(String workingDirectory);

    Map<String, String> getEnvs();

    void setEnvs(Map<String, String> envs);

    String getInterpreterPath();

    void setInterpreterPath(String path);
}