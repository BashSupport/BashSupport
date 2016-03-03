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
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;

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
        if (psiFile == null || !(psiFile instanceof BashFile)) {
            return false;
        }

        VirtualFile file = location.getVirtualFile();
        if (file == null) {
            return false;
        }

        sourceElement.set(psiFile);

        configuration.setName(location.getVirtualFile().getPresentableName());
        configuration.setScriptName(file.getPath());

        if (file.getParent() != null) {
            configuration.setWorkingDirectory(file.getParent().getPath());
        }

        Module module = context.getModule();
        if (module != null) {
            configuration.setModule(module);
        }

        //check the location given by the actual Bash file
        BashFile bashFile = (BashFile) psiFile;
        BashShebang shebang = bashFile.findShebang();

        if (shebang != null) {
            String shebandShell = shebang.shellCommand(false);

            if ((BashInterpreterDetection.instance().isSuitable(shebandShell))) {
                configuration.setInterpreterPath(shebandShell);

                configuration.setInterpreterOptions(shebang.shellCommandParams());
            }
        }

        //fallback location if none was found
        if (StringUtil.isEmptyOrSpaces(configuration.getInterpreterPath())) {
            String bashPath = BashInterpreterDetection.instance().findBestLocation();
            configuration.setInterpreterPath(StringUtils.trimToEmpty(bashPath));
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
