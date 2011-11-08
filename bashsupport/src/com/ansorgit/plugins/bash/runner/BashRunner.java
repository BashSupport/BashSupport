/*******************************************************************************
 * Copyright 2011 Joachim Ansorg, mail@ansorg-it.com
 * File: BashRunner.java, Class: BashRunner
 * Last modified: 2011-04-30 16:33
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

package com.ansorgit.plugins.bash.runner;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.ExecutionResult;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.runners.DefaultProgramRunner;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.execution.runners.RunContentBuilder;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

/**
 * This code is based on the intellij-batch plugin.
 *
 * @author wibotwi, jansorg
 */
public class BashRunner extends DefaultProgramRunner {

    @NotNull
    public String getRunnerId() {
        return "BashRunner";
    }

    public boolean canRun(@NotNull String executorId, @NotNull RunProfile profile) {
        return executorId.equals(DefaultRunExecutor.EXECUTOR_ID) && profile instanceof BashRunConfiguration;
    }


    protected RunContentDescriptor doExecute(final Project project, final Executor executor, final RunProfileState state, final RunContentDescriptor contentToReuse,
                                             final ExecutionEnvironment env) throws ExecutionException {

        FileDocumentManager.getInstance().saveAllDocuments();

        ExecutionResult executionResult = state.execute(executor, this);
        if (executionResult == null) {
            return null;
        }

        final RunContentBuilder contentBuilder = new RunContentBuilder(project, this, executor);
        contentBuilder.setExecutionResult(executionResult);
        contentBuilder.setEnvironment(env);

        return contentBuilder.showRunContent(contentToReuse);
    }
}