package com.ansorgit.plugins.bash.editor.inspections.quickfix;

import com.ansorgit.plugins.bash.BashTestUtils;
import com.ansorgit.plugins.bash.editor.inspections.inspections.SimpleVarUsageInspection;
import com.intellij.codeInsight.daemon.quickFix.LightQuickFixParameterizedTestCase;
import com.intellij.codeInspection.InspectionEP;
import com.intellij.codeInspection.InspectionProfileEntry;
import com.intellij.codeInspection.LocalInspectionEP;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ReplaceVarWithParamExpansionQuickfixTest extends LightQuickFixParameterizedTestCase {

    @Override
    protected void doSingleTest(String fileSuffix, String testDataPath) {
        enableInspectionTools(SimpleVarUsageInspection.class);

        super.doSingleTest(fileSuffix, testDataPath);
    }

    @Override
    protected boolean isRunInWriteAction() {
        return true;
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return BashTestUtils.getBasePath();
    }

    @Override
    protected String getBasePath() {
        return "/quickfixes/replaceVarWithParamExpansionQuickfix";
    }

    //copied from 162.x intellij-community branch to stay compatible
    protected void enableInspectionTools(@NotNull Class<?>... classes) {
        final InspectionProfileEntry[] tools = new InspectionProfileEntry[classes.length];

        final List<InspectionEP> eps = ContainerUtil.newArrayList();
        ContainerUtil.addAll(eps, Extensions.getExtensions(LocalInspectionEP.LOCAL_INSPECTION));
        ContainerUtil.addAll(eps, Extensions.getExtensions(InspectionEP.GLOBAL_INSPECTION));

        next:
        for (int i = 0; i < classes.length; i++) {
            for (InspectionEP ep : eps) {
                if (classes[i].getName().equals(ep.implementationClass)) {
                    tools[i] = ep.instantiateTool();
                    continue next;
                }
            }
            throw new IllegalArgumentException("Unable to find extension point for " + classes[i].getName());
        }

        enableInspectionTools(tools);
    }
}