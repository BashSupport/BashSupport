package com.ansorgit.plugins.bash.runner;

import com.ansorgit.plugins.bash.lang.psi.api.BashFile;
import com.ansorgit.plugins.bash.util.BashInterpreterDetection;
import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.actions.RunConfigurationProducer;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
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

        configuration.setName(file.getPresentableName());
        configuration.setScriptName(file.getPath());
        if (file.getParent() != null) {
            configuration.setWorkingDirectory(file.getParent().getPath());
        }

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
        PsiElement psiLocation = context.getPsiLocation();

        return psiLocation != null && psiLocation.getContainingFile() instanceof BashFile;
    }
}
