package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

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

        VirtualFile file = location.getVirtualFile();
        if (file == null) {
            return false;
        }

        sourceElement.set(location.getPsiElement().getContainingFile());

        configuration.setName(location.getVirtualFile().getPresentableName());
        configuration.setScriptName(file.getPath());

        if (file.getParent() != null) {
            configuration.setWorkingDirectory(file.getParent().getPath());
        }

        //fixme set the shebang line as interpreter and also set the interpreter options
        if (StringUtil.isEmptyOrSpaces(configuration.getInterpreterPath())) {
            configuration.setInterpreterPath(new BashInterpreterDetection().findBestLocation());
        }

        Module module = context.getModule();
        if (module != null) {
            configuration.setModule(module);
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
