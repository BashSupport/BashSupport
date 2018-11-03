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

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.lang.psi.api.BashShebang;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Bash run config producer which looks at the current context to create a new run configuation.
 */
public class BashRunConfigProducer extends RunConfigurationProducer<BashRunConfiguration> {
    public BashRunConfigProducer() {
        super(BashConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(BashRunConfiguration configuration, ConfigurationContext context, Ref<PsiElement> sourceElement) {
        Location location = context.getLocation();
        if (location == null) {
            return false;
        }

        PsiElement psiElement = location.getPsiElement();
        if (!psiElement.isValid()) {
            return false;
        }

        PsiFile psiFile = psiElement.getContainingFile();
        if (!(psiFile instanceof BashFile)) {
            return false;
        }
        sourceElement.set(psiFile);

        VirtualFile file = location.getVirtualFile();
        if (file == null) {
            return false;
        }

        configuration.setName(file.getPresentableName());
        configuration.setScriptName(VfsUtilCore.virtualToIoFile(file).getAbsolutePath());

        if (file.getParent() != null) {
            configuration.setWorkingDirectory(VfsUtilCore.virtualToIoFile(file.getParent()).getAbsolutePath());
        }

        Module module = context.getModule();
        if (module != null) {
            configuration.setModule(module);
        }

        // check the location given by the shebang line
        // do this only if the project interpreter isn't used because we don't want to add the options
        // because it would mess up the execution when the project interpreter was used.
        // options might be added by a shebang like "/usr/bin/env bash".
        // also, we don't want to override the defaults of the template run configuration
        if (!configuration.isUseProjectInterpreter() && configuration.getInterpreterPath().isEmpty()) {
            BashFile bashFile = (BashFile) psiFile;
            BashShebang shebang = bashFile.findShebang();
            if (shebang != null) {
                String shebandShell = shebang.shellCommand(false);

                if ((BashInterpreterDetection.instance().isSuitable(shebandShell))) {
                    configuration.setInterpreterPath(shebandShell);
                    configuration.setInterpreterOptions(shebang.shellCommandParams());
                }
            }
        }

        return true;
    }

    @Override
    public boolean isConfigurationFromContext(BashRunConfiguration configuration, ConfigurationContext context) {
        Location location = context.getLocation();
        if (location == null) {
            return false;
        }

        //fixme file checks needs to check the properties

        VirtualFile file = location.getVirtualFile();
        return file != null && FileUtil.pathsEqual(file.getPath(), configuration.getScriptName());
    }
}
